package org.simpleframework.service.build.extract;

import org.simpleframework.service.context.Context;

public class ContextExtractor implements Extractor<Context> {

   @Override
   public Context extract(Parameter parameter, Context context) {
      Class type = parameter.getType();

      if (type == Context.class) {
         return context;
      }
      return null;
   }

   @Override
   public boolean accept(Parameter parameter) {
      Class type = parameter.getType();

      if (type == Context.class) {
         return true;
      }
      return false;
   }
}
