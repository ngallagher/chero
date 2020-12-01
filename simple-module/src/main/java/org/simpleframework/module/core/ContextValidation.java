package org.simpleframework.module.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ContextValidation implements Validation {

   private final Interpolator interpolator;
   private final TokenFilter filter;
   private final Set<String> errors;

   public ContextValidation(Context context) {
      this.filter = new ContextFilter(context);
      this.interpolator = new Interpolator(filter);
      this.errors = new HashSet<String>();
   }

   @Override
   public boolean isValid() {
      return errors.isEmpty();
   }

   @Override
   public Iterator<String> iterator() {
      return errors.iterator();
   }

   @Override
   public void addError(String error) {
      String message = interpolator.interpolate(error);

      if (message != null) {
         errors.add(message);
      }
   }

   @Override
   public Set<String> getErrors() {
      return errors;
   }
}
