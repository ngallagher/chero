package org.simpleframework.resource.build;

import org.simpleframework.module.core.Context;
import org.simpleframework.resource.action.Action;
import org.simpleframework.resource.action.ActionResolver;

public class ActionBuilder implements ActionResolver {

   private final MethodResolver filters;
   private final MethodResolver actions;

   public ActionBuilder(MethodResolver actions) {
      this(actions, new EmptyResolver());
   }

   public ActionBuilder(MethodResolver actions, MethodResolver filters) {
      this.filters = filters;
      this.actions = actions;
   }

   public Action resolve(Context context) throws Exception {
      Iterable<MethodDispatcher> dispatchers = filters.resolveBestLast(context);
      MethodDispatcher dispatcher = actions.resolveBest(context);

      if (dispatcher != null) {
         return new MethodAction(dispatchers, dispatcher);
      }
      return null;
   }

}
