package org.simpleframework.module.resource.container;

import static org.simpleframework.http.Method.CONNECT;

import java.util.UUID;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.resource.Resource;
import org.simpleframework.module.resource.ResourceMatcher;

class RequestRouter {

   private final ResourceMatcher matcher;
   private final String session;
   
   public RequestRouter(ResourceMatcher matcher, String session) {
      this.matcher = matcher;
      this.session = session;
   }

   public void route(Request request, Response response) throws Throwable {
      String method = request.getMethod();
      Cookie cookie = request.getCookie(session);
      
      if(cookie == null) {
         String value = UUID.randomUUID().toString();
         response.setCookie(session, value);
      }
      Resource resource = matcher.match(request, response);
      
      if(resource == null) {
         throw new RuntimeException("Could not find resource for " + request);
      }
      if(resource.handle(request, response)) {
         if(!method.equalsIgnoreCase(CONNECT)) {
            response.close();
         }
      }
   }
}