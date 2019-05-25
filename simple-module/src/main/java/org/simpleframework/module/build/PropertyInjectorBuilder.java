package org.simpleframework.module.build;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.common.DependencyManager;
import org.simpleframework.module.context.Validator;
import org.simpleframework.module.extract.Extractor;

public class PropertyInjectorBuilder {
   
   private final ExtractorResolver resolver;
   private final PropertyScanner scanner;
   private final Validator validator;
   
   public PropertyInjectorBuilder(DependencyManager manager, ConstructorScanner scanner, List<Extractor> extractors, Validator validator) {
      this.resolver = new ExtractorResolver(manager, scanner, extractors);
      this.scanner = new PropertyScanner();
      this.validator = validator;
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
