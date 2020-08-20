package org.simpleframework.resource.container;

import static org.simpleframework.http.Method.CONNECT;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.resource.Resource;
import org.simpleframework.resource.ResourceMatcher;

class RequestRouter {

   private final ResourceMatcher matcher;
   
   public RequestRouter(ResourceMatcher matcher) {
      this.matcher = matcher;
   }

   public void route(Request request, Response response) throws Throwable {
      String method = request.getMethod();
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