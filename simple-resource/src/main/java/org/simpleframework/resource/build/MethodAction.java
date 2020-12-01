package org.simpleframework.resource.build;

import org.simpleframework.module.core.ContextFilter;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Interpolator;
import org.simpleframework.module.core.TokenFilter;
import org.simpleframework.resource.action.Action;

public class MethodAction implements Action {

   private final Iterable<MethodDispatcher> interceptors;
   private final MethodDispatcher dispatcher;

   public MethodAction(Iterable<MethodDispatcher> filters, MethodDispatcher dispatcher) {
      this.interceptors = filters;
      this.dispatcher = dispatcher;
   }

   public float score(Context context) throws Exception {
      return dispatcher.score(context);
   }

   @Override
   public Object execute(Context context) throws Exception {
      TokenFilter filter = new ContextFilter(context);
      Interpolator interpolator = new Interpolator(filter);

      for (MethodDispatcher interceptor : interceptors) {
         Object result = interceptor.execute(context);

         if (result != null) {
            return interpolator.interpolate(result);
         }
      }
      Object result = dispatcher.execute(context);

      if (result != null) {
         return interpolator.interpolate(result);
      }
      return null;
   }
}
