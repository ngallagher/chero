package org.simpleframework.resource.container;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.simpleframework.resource.ExceptionHandler;
import org.simpleframework.resource.ResourceMatcher;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.trace.Trace;

import static org.simpleframework.http.Method.CONNECT;
import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Protocol.SERVER;
import static org.simpleframework.resource.ResourceEvent.ERROR;

class ServerContainer implements Container {

   private final ExceptionHandler handler;
   private final SessionManager manager;
   private final RequestRouter router;
   private final Logger logger;
   private final String name;
   
   public ServerContainer(ResourceMatcher matcher, Logger logger, String name, String session) {
      this.router = new RequestRouter(matcher);
      this.manager = new SessionManager(session);
      this.handler = new ExceptionHandler();
      this.logger = logger;
      this.name = name;
   }

   @Override
   public void handle(Request request, Response response) {
      long time = System.currentTimeMillis();
      String method = request.getMethod();
      Channel channel = request.getChannel();
      Trace trace = channel.getTrace();
      
      try {
         logger.log(request);
         manager.resolve(request, response);
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