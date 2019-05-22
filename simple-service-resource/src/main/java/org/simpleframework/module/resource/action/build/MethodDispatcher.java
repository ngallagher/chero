package org.simpleframework.module.resource.action.build;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.simpleframework.http.Request;
import org.simpleframework.module.build.ComponentBuilder;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.Model;

public class MethodDispatcher {

   private final Queue<ComponentBuilder> builders;
   private final MethodExecutor executor;

   public MethodDispatcher(List<ComponentBuilder> builders, MethodExecutor executor) {
      this.builders = new LinkedList<ComponentBuilder>(builders);
      this.executor = executor;
   }

   public Object execute(Context context) throws Exception {
      Model model = context.getModel();
      Request request = model.get(Request.class);
      
      if(request == null) {
         throw new IllegalStateException("Could not get request from model");
      }
      Object value = build(context);

      if (value == null) {
         throw new IllegalStateException("Could not create a component for " + request);
      }
      return executor.execute(value, context);
   }

   public float score(Context context) throws Exception {
      return executor.score(context);
   }

   private Object build(Context context) throws Exception {
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
   public String toString() {
      return executor.toString();
   }
}
