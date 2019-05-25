package org.simpleframework.module.extract;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.simpleframework.module.build.Argument;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.core.Context;

public class ComponentExtractor implements Extractor<Object> {

   private final Queue<Function> builders;
   private final Class type;

   public ComponentExtractor(List<Function> builders, Class type) {
      this.builders = new LinkedList<Function>(builders);
      this.type = type;
   }

   @Override
   public Object extract(Argument argument, Context context) throws Exception {
      Function match = builders.peek();
      float best = 0f;

      for (Function builder : builders) {
         float score = builder.getScore(context);

         if (score > best) {
            match = builder;
            best = score;
         }
      }
      return match.getValue(context);
   }

   @Override
   public boolean accept(Argument argument) throws Exception {
      Class expect = argument.getType();

      if (type == expect) {
         return true;
      }
      return false;
   }
}
