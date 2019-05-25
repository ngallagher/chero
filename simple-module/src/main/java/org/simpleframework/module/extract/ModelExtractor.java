package org.simpleframework.module.extract;

import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;

public class ModelExtractor implements Extractor<Model> {

   @Override
   public Model extract(Argument argument, Context context) {
      Model model = context.getModel();
      Class type = argument.getType();

      if (type == Model.class) {
         return model;
      }
      return null;
   }

   @Override
   public boolean accept(Argument argument) {
      Class type = argument.getType();

      if (type == Model.class) {
         return true;
      }
      return false;
   }
}
