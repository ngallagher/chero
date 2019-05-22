package org.simpleframework.service.build;

import java.util.List;
import java.util.Set;

import org.simpleframework.service.build.validate.Validator;
import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.Validation;

public class PropertyInjector {

   private final List<FieldSetter> setters;
   private final Validator validator;

   public PropertyInjector(List<FieldSetter> setters, Validator validator) {
      this.validator = validator;
      this.setters = setters;
   }

   public void inject(Object instance, Context context) throws Exception {
      for (FieldSetter setter : setters) {
         if (setter.valid(context)) {
            setter.set(instance, context);
         }
      }
      if (validator != null) {
         Set<String> violations = validator.validateObject(instance);
         Validation validation = context.getValidation();

         for (String violation : violations) {
            validation.addError(violation);
         }
      }
   }

   public boolean valid(Context context) throws Exception {
      for (FieldSetter setter : setters) {
         if (setter.valid(context)) {
            return false;
         }
      }
      return true;
   }
}
