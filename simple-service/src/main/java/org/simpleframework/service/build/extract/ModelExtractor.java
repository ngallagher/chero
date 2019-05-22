package org.simpleframework.service.build.extract;

import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.Model;

public class ModelExtractor implements Extractor<Model> {

   @Override
   public Model extract(Parameter parameter, Context context) {
      Model model = context.getModel();
      Class type = parameter.getType();

      if (type == Model.class) {
         return model;
      }
      return null;
   }

   @Override
   public boolean accept(Parameter parameter) {
      Class type = parameter.getType();

      if (type == Model.class) {
         return true;
      }
      return false;
   }
}
