package org.simpleframework.module.extract;

import java.util.List;

import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;

public class DependencyListExtractor implements Extractor<List> {

   private final ComponentManager manager;
   private final Class entry;

   public DependencyListExtractor(ComponentManager manager, Class entry) {
      this.manager = manager;
      this.entry = entry;
   }

   @Override
   public List extract(Argument argument, Context context) {
      Class expect = argument.getType();
      Inject annotation = argument.getAnnotation(Inject.class);
      boolean constructor = argument.isConstructor();
      
      if(expect == List.class) {
         if(annotation != null) {
            return manager.resolveAll(entry);
         }
         if(constructor) {
            return manager.resolveAll(entry);
         }
      }
      return null;
   }

   @Override
   public boolean accept(Argument argument) {
      Class expect = argument.getType();
      Class element = argument.getEntry();
      Inject annotation = argument.getAnnotation(Inject.class);
      boolean constructor = argument.isConstructor();

      if(annotation != null || constructor) {
         return expect == List.class && element == entry;
      }
      return false;
   }
}

