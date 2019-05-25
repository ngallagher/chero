package org.simpleframework.module.extract;

import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Validation;

public class ValidationExtractor implements Extractor<Validation> {

   @Override
   public Validation extract(Argument argument, Context context) {
      Validation validation = context.getValidation();
      Class type = argument.getType();

      if (type == Validation.class) {
         return validation;
      }
      return null;
   }

   @Override
   public boolean accept(Argument argument) {
      Class type = argument.getType();

      if (type == Validation.class) {
         return true;
      }
      return false;
   }
}
