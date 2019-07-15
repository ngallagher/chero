package org.simpleframework.module.resource.action.build;

import java.util.Set;

import org.simpleframework.http.Request;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.resource.action.Schema;

public class MethodDispatcher {

   private final MethodDescription description;
   private final MethodExecutor executor;

   public MethodDispatcher(MethodMatcher matcher, MethodHeader header, Function function) {
      this.executor = new MethodExecutor(matcher, header, function);
      this.description = new MethodDescription(matcher, header, function);
   }

   public void define(Schema schema) throws Exception {
      String method = description.getMethod();
      Set<ActionDescription> actions = schema.getActions(method);
      
      if(actions == null) {
         throw new IllegalStateException("Could not define action for " + executor);
      }
      actions.add(description);
   }
   
   public Object execute(Context context) throws Exception {
      Model model = context.getModel();
      Request request = model.get(Request.class);
      
      if(request == null) {
         throw new IllegalStateException("Could not get request for " + executor);
      }
      return executor.execute(context);
   }

   public float score(Context context) throws Exception {
      return executor.score(context);
   }

   @Override
   public String toString() {
      return executor.toString();
   }
}
