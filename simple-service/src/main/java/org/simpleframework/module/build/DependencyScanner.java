package org.simpleframework.module.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.module.DependencyManager;
import org.simpleframework.module.annotation.DefaultValue;
import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.annotation.Required;
import org.simpleframework.module.build.extract.ComponentExtractor;
import org.simpleframework.module.build.extract.DependencyExtractor;
import org.simpleframework.module.build.extract.DependencyListExtractor;
import org.simpleframework.module.build.extract.Extractor;
import org.simpleframework.module.build.extract.Parameter;
import org.simpleframework.module.build.extract.ParameterBuilder;
import org.simpleframework.module.build.extract.StringConverter;
import org.simpleframework.module.build.validate.AnnotationValidator;
import org.simpleframework.module.build.validate.Validator;

public class DependencyScanner {

   private final Map<Class, List<ComponentBuilder>> cache;
   private final List<Extractor> extractors;
   private final StringConverter converter;
   private final DependencyManager manager;
   private final Validator validator;

   public DependencyScanner(DependencyManager manager, List<Extractor> extractors) {
      this(manager, extractors, new AnnotationValidator());
   }

   public DependencyScanner(DependencyManager manager, List<Extractor> extractors, Validator validator) {
      this.cache = new ConcurrentHashMap<Class, List<ComponentBuilder>>();
      this.converter = new StringConverter();
      this.extractors = extractors;
      this.validator = validator;
      this.manager = manager;
   }
   
   public List<ComponentBuilder> createBuilders(Class type) throws Exception {
      List<ComponentBuilder> matches = new LinkedList<ComponentBuilder>();
   
      if(type != null) {
         List<ComponentBuilder> builders = cache.get(type);
      
         if (builders == null) {
            Constructor[] factories = type.getDeclaredConstructors();
   
            for (Constructor factory : factories) {
               ComponentBuilder builder = createBuilder(type, factory);
   
               if (builder != null) {
                  matches.add(builder);
               }
            }
            if(matches.isEmpty()) {
               throw new IllegalStateException("Could not construct " + type);
            }
            cache.put(type, matches);
            return matches;
         }
         return builders;
      }
      return matches;
   }

   private boolean isDependency(Parameter parameter) {
      try {
         Inject annotation = parameter.getAnnotation(Inject.class);
         boolean constructor = parameter.isConstructor();
         Class type = parameter.getType();

         if(annotation != null) {
            String name = annotation.value();
            return isDependency(type, name);
         }
         if(constructor) {
            return isDependency(type, null);
         }
         return false;
      } catch (Exception e) {
         return false;
      }
   }
   
   private boolean isDependency(Class type, String name) {
      try {
         if(name != null) {
            int length = name.length();
            
            if(length > 0) {
               return manager.resolve(type, name) != null;
            }
         }
         return manager.resolve(type) != null;
      } catch (Exception e) {
         return false;
      } 
   }

   private boolean isComponent(Parameter parameter) {
      return false;
   }

   private AnnotationContext createData(Annotation[] annotations) throws Exception {
      AnnotationContext data = new AnnotationContext();
      
      for (Annotation annotation : annotations) {
         if (annotation instanceof Required) {
            data.setRequired(true);
         } else if (annotation instanceof DefaultValue) {
            DefaultValue value = (DefaultValue) annotation;
            String text = value.value();

            data.setDefault(text);
         } 
         data.addAnnotation(annotation);
      }
      return data;
   }

   private Property createProperty(Class type, Class entry, Annotation[] annotations) throws Exception {
      AnnotationContext data = createData(annotations);
      Map<Class, Annotation> map = data.getAnnotations();
      String value = data.getDefault();
      boolean required = data.isRequired();

      return new Property(type, entry, value, map, required);
   }

   private Parameter createParameter(Class type, Class entry, Annotation[] annotations, boolean constructor) throws Exception {
      AnnotationContext data = createData(annotations);
      Map<Class, Annotation> map = data.getAnnotations();
      String value = data.getDefault();
      boolean required = data.isRequired();

      return new Parameter(type, entry, value, map, constructor, required);
   }

   private Extractor createExtractor(Class type, Parameter parameter) throws Exception {
      for (Extractor extractor : extractors) {
         if (extractor.accept(parameter)) {
            return extractor;
         }
      }
      return createDefaultExtractor(type, parameter);
   }

   private Extractor createDefaultExtractor(Class parent, Parameter parameter) throws Exception {
      Class type = parameter.getType();
      Class entry = parameter.getEntry();
      
      if (!converter.accept(type)) {
         if (isComponent(parameter)) {
            List<ComponentBuilder> builders = createBuilders(type);
            
            if(!builders.isEmpty()) {
               return new ComponentExtractor(builders, type);
            }
            return null;
         }
         if(parameter.isList()) {
            return new DependencyListExtractor(manager, entry);
         }   
         return new DependencyExtractor(manager, type);
      }
      return null;
   }
   
   private Class createDependency(Type type) throws Exception  {
      if(ParameterizedType.class.isInstance(type)) {
         ParameterizedType real = (ParameterizedType)type;
         Type[] types = real.getActualTypeArguments();
         
         if(types.length > 0) {
            return (Class)types[0];
         }
      }
      return null;
   }

   private ComponentBuilder createBuilder(Class type, Constructor factory) throws Exception {
      ParameterBuilder extractor = createExtractor(factory);
      
      if(extractor != null) {
         PropertyInjector injector = createInjector(type);
   
         if (!factory.isAccessible()) {
            factory.setAccessible(true);
         }
         return new DependencyBuilder(manager, extractor, injector, factory);
      }
      return null;
   }

   private FieldSetter createSetter(Field field) throws Exception {
      Annotation[] list = field.getAnnotations();
      Class type = field.getType();
      Class parent = field.getDeclaringClass();
      Property property = createProperty(type, null, list);
      
      if(property.isInjectable()) {
         Extractor extractor = createExtractor(parent, property);
   
         if (!field.isAccessible()) {
            field.setAccessible(true);
         }
         return new FieldSetter(property, extractor, field);
      }
      return null;
   }

   private PropertyInjector createInjector(Class type) throws Exception {
      List<FieldSetter> setters = new LinkedList<FieldSetter>();

      while (type != null) {
         Field[] fields = type.getDeclaredFields();

         for (Field field : fields) {
            FieldSetter setter = createSetter(field);

            if (setter != null) {
               setters.add(setter);
            }
         }
         type = type.getSuperclass();
      }
      return new PropertyInjector(setters, validator);
   }

   private Parameter[] createParameters(Constructor factory) throws Exception {
      Annotation[][] annotations = factory.getParameterAnnotations();
      Type[] types = factory.getGenericParameterTypes();
      Class[] classes = factory.getParameterTypes();

      if (types.length > 0) {
         Parameter[] parameters = new Parameter[types.length];

         for (int i = 0; i < types.length; i++) {
            Class entry = createDependency(types[i]);
            Class type = classes[i];
            
            parameters[i] = createParameter(type, entry, annotations[i], true);
         }
         return parameters;
      }
      return new Parameter[] {};
   }

   private ParameterBuilder createExtractor(Constructor factory) throws Exception {
      Parameter[] parameters = createParameters(factory);
      Class parent = factory.getDeclaringClass();

      if (parameters.length > 0) {
         Extractor[] extractors = new Extractor[parameters.length];

         for (int i = 0; i < parameters.length; i++) {
            extractors[i] = createExtractor(parent, parameters[i]);
            
            if(extractors[i] == null) {
               return null;
            }
         }
         return new ParameterBuilder(extractors, parameters);
      }
      return new ParameterBuilder();
   }
}
