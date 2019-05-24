package org.simpleframework.module.extract;

import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.build.Parameter;
import org.simpleframework.module.common.DependencyManager;
import org.simpleframework.module.context.Context;

public class DependencyExtractor implements Extractor<Object> {

   private final DependencyManager manager;
   private final Class type;

   public DependencyExtractor(DependencyManager manager, Class type) {
      this.manager = manager;
      this.type = type;
   }

   @Override
   public Object extract(Parameter parameter, Context context) {
      Class type = parameter.getType();
      Inject annotation = parameter.getAnnotation(Inject.class);
      boolean constructor = parameter.isConstructor();
      
      if(annotation != null) {
         String name = annotation.value();
         return extract(type, name);
      }
      if(constructor) {
         return extract(type, null);
      }
      return null;
   }
   
   private Object extract(Class type, String name) {
      try {
         if(name != null) {
            int length = name.length();
            
            if(length > 0) {
               return manager.resolve(type, name);
            }
         }
         return manager.resolve(type);
      } catch (Exception e) {
         return null;
      } 
   }

   @Override
   public boolean accept(Parameter parameter) {
      Class expect = parameter.getType();
      Inject annotation = parameter.getAnnotation(Inject.class);
      boolean constructor = parameter.isConstructor();

      if(annotation != null || constructor) {
         return type == expect;
      }
      return false;
   }
}
