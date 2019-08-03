package org.simpleframework.resource.container;

import static org.simpleframework.http.Method.CONNECT;
import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Protocol.SERVER;
import static org.simpleframework.resource.ResourceEvent.ERROR;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.simpleframework.resource.ExceptionHandler;
import org.simpleframework.resource.ResourceMatcher;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.trace.Trace;

class ServerContainer implements Container {

   private final ExceptionHandler handler;
   private final RequestRouter router;
   private final String name;
   
   public ServerContainer(ResourceMatcher matcher, String name, String session) {
      this.router = new RequestRouter(matcher, session);
      this.handler = new ExceptionHandler();
      this.name = name;
   }

   @Override
   public void handle(Request request, Response response) {
      long time = System.currentTimeMillis();
      String method = request.getMethod();
      Channel channel = request.getChannel();
      Trace trace = channel.getTrace();
      
      try {
         response.setDate(DATE, time);
         response.setValue(SERVER, name);
         response.setDate(DATE, time);
         response.setStatus(Status.OK);
         router.route(request, response);
      } catch (Throwable cause) {
         trace.trace(ERROR, cause); 
         handler.handle(request, response, cause);
         
         try {
            if(!method.equalsIgnoreCase(CONNECT)) {
               response.close();
            }
         } catch (Exception ignore) {
            trace.trace(ERROR, ignore);
         }
      }
   }
}