package org.simpleframework.service.build.extract;

import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.Validation;

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
