package org.simpleframework.module.extract;

import org.simpleframework.module.build.Argument;
import org.simpleframework.module.context.Context;

public class ContextExtractor implements Extractor<Context> {

   @Override
   public Context extract(Argument argument, Context context) {
      Class type = argument.getType();

      if (type == Context.class) {
         return context;
      }
      return null;
   }

   @Override
   public boolean accept(Argument argument) {
      Class type = argument.getType();

      if (type == Context.class) {
         return true;
      }
      return false;
   }
}
