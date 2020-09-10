package org.simpleframework.module.extract;

import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;

public interface Extractor<T> {
   boolean accept(Argument argument) throws Exception;
   T extract(Argument argument, Context context) throws Exception;

   default float score(Argument argument, Context context) throws Exception {
      T result = extract(argument, context);

      if(result == null) {
         if (argument.isRequired()) {
            return -1;
         }
         return 0;
      }
      return 1;
   }
}
