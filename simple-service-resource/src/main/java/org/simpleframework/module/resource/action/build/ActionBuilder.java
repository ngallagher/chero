package org.simpleframework.module.resource.action.build;

import java.util.List;

import org.simpleframework.module.context.Context;
import org.simpleframework.module.resource.action.Action;
import org.simpleframework.module.resource.action.ActionResolver;

public class ActionBuilder implements ActionResolver {

   private final MethodResolver interceptors;
   private final MethodResolver actions;

   public ActionBuilder(MethodResolver actions) {
      this(actions, new EmptyResolver());
   }

   public ActionBuilder(MethodResolver actions, MethodResolver interceptors) {
      this.interceptors = interceptors;
      this.actions = actions;
   }

   public Action resolve(Context context) throws Exception {
      List<MethodDispatcher> dispatchers = interceptors.resolveBestLast(context);
      MethodDispatcher dispatcher = actions.resolveBest(context);

      if (dispatcher != null) {
         return new MethodAction(dispatchers, dispatcher);
      }
      return null;
   }

}
