package org.simpleframework.module.build;

import java.util.List;
import java.util.Set;

import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Validation;
import org.simpleframework.module.core.Validator;

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
