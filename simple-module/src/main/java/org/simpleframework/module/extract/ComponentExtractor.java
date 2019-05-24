package org.simpleframework.module.extract;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.simpleframework.module.build.ComponentBuilder;
import org.simpleframework.module.build.Parameter;
import org.simpleframework.module.context.Context;

public class ComponentExtractor implements Extractor<Object> {

   private final Queue<ComponentBuilder> builders;
   private final Class type;

   public ComponentExtractor(List<ComponentBuilder> builders, Class type) {
      this.builders = new LinkedList<ComponentBuilder>(builders);
      this.type = type;
   }

   @Override
   public Object extract(Parameter parameter, Context context) throws Exception {
      ComponentBuilder match = builders.peek();
      float best = 0f;

      for (ComponentBuilder builder : builders) {
         float score = builder.score(context);

         if (score > best) {
            match = builder;
            best = score;
         }
      }
      return match.build(context);

   }

   @Override
   public boolean accept(Parameter parameter) throws Exception {
      Class expect = parameter.getType();

      if (type == expect) {
         return true;
      }
      return false;
   }
}