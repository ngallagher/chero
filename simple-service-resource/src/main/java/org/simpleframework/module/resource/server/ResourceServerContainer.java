package org.simpleframework.module.resource.server;

import static org.simpleframework.http.Method.CONNECT;
import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Protocol.SERVER;
import static org.simpleframework.module.resource.ResourceEvent.ERROR;
import static org.simpleframework.module.resource.SessionConstants.SESSION_ID;

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

public class ResourceServerContainer implements Container {
   
   private static final String SERVER_NAME = "Apache/2.2.14";

   private final ResourceMatcher matcher;

   public ResourceServerContainer(ResourceMatcher matcher) {
      this.matcher = matcher;
   }

   @Override
   public void handle(Request request, Response response) {
      long time = System.currentTimeMillis();
      String method = request.getMethod();
      Channel channel = request.getChannel();
      Trace trace = channel.getTrace();
      
      try {
         Resource resource = matcher.match(request, response);
         Cookie cookie = request.getCookie(SESSION_ID);
         
         if(cookie == null) {
            String value = UUID.randomUUID().toString();
            response.setCookie(SESSION_ID, value);
         }
         response.setDate(DATE, time);
         response.setValue(SERVER, SERVER_NAME);
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