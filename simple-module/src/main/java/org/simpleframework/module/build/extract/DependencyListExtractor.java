package org.simpleframework.module.build.extract;

import java.util.List;

import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.common.DependencyManager;
import org.simpleframework.module.context.Context;

public class DependencyListExtractor implements Extractor<List> {

   private final DependencyManager manager;
   private final Class entry;

   public DependencyListExtractor(DependencyManager manager, Class entry) {
      this.manager = manager;
      this.entry = entry;
   }

   @Override
   public List extract(Parameter parameter, Context context) {
      Class expect = parameter.getType();
      Inject annotation = parameter.getAnnotation(Inject.class);
      boolean constructor = parameter.isConstructor();
      
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
   public boolean accept(Parameter parameter) {
      Class expect = parameter.getType();
      Class element = parameter.getEntry();
      Inject annotation = parameter.getAnnotation(Inject.class);
      boolean constructor = parameter.isConstructor();

      if(annotation != null || constructor) {
         return expect == List.class && element == entry;
      }
      return false;
   }
}

