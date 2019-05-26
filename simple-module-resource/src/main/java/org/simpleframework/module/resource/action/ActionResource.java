package org.simpleframework.module.resource.action;

import static org.simpleframework.http.Method.CONNECT;
import static org.simpleframework.module.resource.ResourceEvent.ERROR;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.resource.Resource;
import org.simpleframework.module.resource.action.write.ResponseWriter;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.trace.Trace;

public class ActionResource implements Resource {

   private final ResponseWriter router;
   private final Context context;
   private final Action action;

   public ActionResource(ResponseWriter router, Action action, Context context) {
      this.context = context;
      this.action = action;
      this.router = router;
   }

   @Override
   public boolean handle(Request request, Response response) throws Throwable {
      try {
         Object result = action.execute(context);

         if (!response.isCommitted()) {
            Throwable cause = context.getError();

            if (result != null) {
               context.setResult(result);
               return router.write(response, result);
            } 
            return router.write(response, cause);            
         }
      } catch (Throwable cause) {
         Channel channel = request.getChannel();
         Trace trace = channel.getTrace();
         
         trace.trace(ERROR, cause);

         if (!response.isCommitted()) {
            context.setError(cause);
            router.write(response, cause);
         }
      }
      return true;
   }
}
