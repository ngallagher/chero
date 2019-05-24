package org.simpleframework.module.extract;

import org.simpleframework.module.build.Parameter;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.Validation;

public class ValidationExtractor implements Extractor<Validation> {

   @Override
   public Validation extract(Parameter parameter, Context context) {
      Validation validation = context.getValidation();
      Class type = parameter.getType();

      if (type == Validation.class) {
         return validation;
      }
      return null;
   }

   @Override
   public boolean accept(Parameter parameter) {
      Class type = parameter.getType();

      if (type == Validation.class) {
         return true;
      }
      return false;
   }
}
