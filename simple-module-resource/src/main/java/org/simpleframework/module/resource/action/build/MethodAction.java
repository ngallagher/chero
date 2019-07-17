package org.simpleframework.module.resource.action.build;

import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Interpolator;
import org.simpleframework.module.resource.action.Action;

public class MethodAction implements Action {

   private final Iterable<MethodDispatcher> interceptors;
   private final MethodDispatcher dispatcher;

   public MethodAction(Iterable<MethodDispatcher> interceptors, MethodDispatcher dispatcher) {
      this.interceptors = interceptors;
      this.dispatcher = dispatcher;
   }

   public float score(Context context) throws Exception {
      return dispatcher.score(context);
   }

   @Override
   public Object execute(Context context) throws Exception {
      Interpolator interpolator = new Interpolator(context);

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
