package org.simpleframework.module.resource.container;

import static org.simpleframework.http.Method.CONNECT;
import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Protocol.SERVER;
import static org.simpleframework.module.resource.ResourceEvent.ERROR;

import java.util.UUID;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.simpleframework.module.resource.Resource;
import org.simpleframework.module.resource.ResourceMatcher;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.trace.Trace;

public class RequestHandler implements Container {

   private final ResourceMatcher matcher;
   private final String session;
   private final String name;
   
   public RequestHandler(ResourceMatcher matcher, String name, String session) {
      this.matcher = matcher;
      this.session = session;
      this.name = name;
   }

   @Override
   public void handle(Request request, Response response) {
      long time = System.currentTimeMillis();
      String method = request.getMethod();
      Channel channel = request.getChannel();
      Trace trace = channel.getTrace();
      
      try {
         Resource resource = matcher.match(request, response);
         Cookie cookie = request.getCookie(session);
         
         if(cookie == null) {
            String value = UUID.randomUUID().toString();
            response.setCookie(session, value);
         }
         response.setDate(DATE, time);
         response.setValue(SERVER, name);
         response.setDate(DATE, time);
         
         if(resource != null) {
            response.setStatus(Status.OK);
            resource.handle(request, response);
         } else {
            throw new RuntimeException("Default response here " + request);
         }
      } catch (Throwable cause) {
         trace.trace(ERROR, cause);
      } finally {
         try {
            if(!method.equalsIgnoreCase(CONNECT)) {
               response.close();
            }
         } catch (Exception cause) {
            trace.trace(ERROR, cause);
         }
      }
   }
}