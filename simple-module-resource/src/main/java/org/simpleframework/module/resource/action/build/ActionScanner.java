package org.simpleframework.module.resource.action.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.module.annotation.DefaultValue;
import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.annotation.Required;
import org.simpleframework.module.build.AnnotationContext;
import org.simpleframework.module.build.ComponentBuilder;
import org.simpleframework.module.build.FieldSetter;
import org.simpleframework.module.build.Parameter;
import org.simpleframework.module.build.ParameterBuilder;
import org.simpleframework.module.build.Property;
import org.simpleframework.module.build.PropertyInjector;
import org.simpleframework.module.common.DependencyManager;
import org.simpleframework.module.context.AnnotationValidator;
import org.simpleframework.module.context.Validator;
import org.simpleframework.module.extract.ComponentExtractor;
import org.simpleframework.module.extract.DependencyExtractor;
import org.simpleframework.module.extract.DependencyListExtractor;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.StringConverter;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Ignore;
import org.simpleframework.module.resource.annotation.Verb;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class ActionScanner {
   
   private final Map<Class, List<ComponentBuilder>> cache;
   private final List<Extractor> extractors;
   private final StringConverter converter;
   private final DependencyManager manager;
   private final PathFormatter formatter;
   private final Validator validator;

   public ActionScanner(DependencyManager manager, List<Extractor> extractors) {
      this(manager, extractors, new AnnotationValidator());
   }

   public ActionScanner(DependencyManager manager, List<Extractor> extractors, Validator validator) {
      this.cache = new ConcurrentHashMap<Class, List<ComponentBuilder>>();
      this.converter = new StringConverter();
      this.formatter = new PathFormatter();
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

   public Multimap<String, MethodDispatcher> createDispatchers(Class<?> type) throws Exception {
      Multimap<String, MethodDispatcher> dispatchers = LinkedHashMultimap.create();

      if (type != null) {
         ComponentType componentType = ComponentType.resolveType(type);
         List<ComponentBuilder> builders = createBuilders(type);
         String typePath = componentType.extractPath(type);

         while (type != null) {
            Method[] methods = type.getDeclaredMethods();

            for (Method method : methods) {
               String methodPath = componentType.extractPath(method);

               if (methodPath != null) {
                  MethodMatcher matcher = createMatcher(method, typePath, methodPath);
                  MethodDispatcher dispatcher = createDispatcher(builders, method, matcher);
                  String pattern = matcher.pattern();

                  if(dispatcher == null) {
                     throw new IllegalStateException("Could not resolve for " + pattern + " on " + method);
                  }
                  dispatchers.put(pattern, dispatcher);
               }
            }
            type = type.getSuperclass();
         }
      }
      return dispatchers;
   }
   
   private MethodMatcher createMatcher(Method method, String typePath, String methodPath) throws Exception {
      Ignore ignore = method.getAnnotation(Ignore.class);
      Annotation[] annotations = method.getAnnotations();
      String methodName = method.getName();
      String parentPath = "/";
      String ignorePath = "";
      
      if (ignore != null) {
         ignorePath = ignore.value();
         formatter.formatPath(ignorePath);
      }
      if (typePath != null) {
         parentPath = formatter.formatPath(typePath);
      }
      if (!methodPath.equals("") && !methodPath.equals("/")) {
         methodPath = formatter.formatPath(methodPath);  
      } else {
         methodPath = formatter.formatPath(methodName);
      }
      for(Annotation annotation : annotations) {
         Class<? extends Annotation> methodVerb = annotation.annotationType();
         
         if(methodVerb.isAnnotationPresent(Verb.class)) {
            return new MethodMatcher(methodVerb, ignorePath, parentPath, methodPath);
         }
      }
      return new MethodMatcher(GET.class, ignorePath, parentPath, methodPath);
 
   }

   private MethodDispatcher createDispatcher(List<ComponentBuilder> builders, Method method, MethodMatcher expression) throws Exception {
      MethodExecutor executor = createExecutor(method, expression);

      if (executor != null) {
         return new MethodDispatcher(builders, executor);
      }
      return null;
   }

   private MethodExecutor createExecutor(Method method, MethodMatcher matcher) throws Exception {
      ParameterBuilder extractor = createExtractor(method);
      
      if(extractor != null) {
         Annotation[] annotations = method.getAnnotations();
         MethodHeader header = createHeader(method, annotations);
         
         if (!method.isAccessible()) {
            method.setAccessible(true);
         }
         return new MethodExecutor(matcher, header, extractor, validator, method);
      }
      return null;
   }

   private MethodHeader createHeader(Method method, Annotation[] annotations) throws Exception {
      MethodHeader header = new MethodHeader();

      for (Annotation annotation : annotations) {
         header.extractHeader(annotation);
      }
      return header;
   }

   private Parameter[] createParameters(Method method) throws Exception {
      Annotation[][] annotations = method.getParameterAnnotations();
      Type[] types = method.getGenericParameterTypes();
      Class[] classes = method.getParameterTypes();

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

   private ParameterBuilder createExtractor(Method method) throws Exception {
      Parameter[] parameters = createParameters(method);
      Class parent = method.getDeclaringClass();

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
   
   private boolean isComponent(Parameter parameter) {
      Class type = parameter.getType();
      ComponentType component = ComponentType.resolveType(type);
      int modifiers = type.getModifiers();

      if (component != null) {
         if (Modifier.isAbstract(modifiers)) {
            return false;
         }
         if (Modifier.isInterface(modifiers)) {
            return false;
         }
         return true;
      }
      return false;
   }
   
   private ComponentBuilder createBuilder(Class type, Constructor factory) throws Exception {
      ParameterBuilder extractor = createExtractor(factory);
      
      if(extractor != null) {
         PropertyInjector injector = createInjector(type);
   
         if (!factory.isAccessible()) {
            factory.setAccessible(true);
         }
         return new ContextBuilder(extractor, injector, factory);
      }
      return null;
   }
}
