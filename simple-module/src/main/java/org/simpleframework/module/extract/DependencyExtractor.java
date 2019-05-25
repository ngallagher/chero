package org.simpleframework.module.extract;

import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;

public class DependencyExtractor implements Extractor<Object> {

   private final ComponentManager manager;
   private final Class type;

   public DependencyExtractor(ComponentManager manager, Class type) {
      this.manager = manager;
      this.type = type;
   }

   @Override
   public Object extract(Argument argument, Context context) {
      Class type = argument.getType();
      Inject annotation = argument.getAnnotation(Inject.class);
      boolean constructor = argument.isConstructor();
      
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
   public boolean accept(Argument argument) {
      Class expect = argument.getType();
      Inject annotation = argument.getAnnotation(Inject.class);
      boolean constructor = argument.isConstructor();

      if(annotation != null || constructor) {
         return type == expect;
      }
      return false;
   }
}
