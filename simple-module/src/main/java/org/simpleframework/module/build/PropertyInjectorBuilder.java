package org.simpleframework.module.build;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.simpleframework.module.core.AnnotationValidator;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Validator;
import org.simpleframework.module.extract.Extractor;

public class PropertyInjectorBuilder {
   
   private final ExtractorResolver resolver;
   private final PropertyScanner scanner;
   private final Validator validator;
   
   public PropertyInjectorBuilder(ComponentManager manager, ConstructorScanner scanner, List<Extractor> extractors, Predicate<Argument> transients) {
      this.resolver = new ExtractorResolver(manager, scanner, extractors, transients);
      this.validator = new AnnotationValidator();
      this.scanner = new PropertyScanner();    
   }

   public PropertyInjector createInjector(Class type) throws Exception {
      List<FieldSetter> setters = new LinkedList<FieldSetter>();

      while (type != null) {
         Field[] fields = type.getDeclaredFields();

         for (Field field : fields) {
            Property property = scanner.createProperty(field);
            FieldSetter setter = createSetter(field);

            if (setter != null) {
               setters.add(setter);
            }
         }
         type = type.getSuperclass();
      }
      return new PropertyInjector(setters, validator);
   }
   
   private FieldSetter createSetter(Field field) throws Exception {
      Property property = scanner.createProperty(field);

      if(property.isInjectable()) {
         Extractor extractor = resolver.resolve(property);
   
         if (!field.isAccessible()) {
            field.setAccessible(true);
         }
         return new FieldSetter(property, extractor, field);
      }
      return null;
   }
}
