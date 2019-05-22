package org.simpleframework.module.resource;

import static org.simpleframework.http.Protocol.UPGRADE;
import static org.simpleframework.http.Protocol.WEBSOCKET;

import java.util.List;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.socket.service.DirectRouter;
import org.simpleframework.http.socket.service.Router;
import org.simpleframework.http.socket.service.Service;
import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.LeastRecentlyUsedCache;
import org.simpleframework.module.resource.annotation.Subscribe;

public class SubscriptionRouter implements Router {
   
   private final Cache<String, Router> routes;
   private final List<Service> services;
   
   public SubscriptionRouter(List<Service> services) {
      this.routes = new LeastRecentlyUsedCache<String, Router>();
      this.services = services;
   }

   @Override
   public Service route(Request request, Response response) {
      String token = request.getValue(UPGRADE);
      
      if(token != null) {
         if(token.equalsIgnoreCase(WEBSOCKET)) {
            Path path = request.getPath();
            String normal = path.getPath();
            Router router = routes.fetch(normal);
            
            if(router == null) {
               for(Service service : services) {
                  Class<?> type = service.getClass();
                  Subscribe label = type.getAnnotation(Subscribe.class);
                  
                  if(label != null) {
                     String pattern = label.value();
                     
                     if(normal.matches(pattern)) {
                        router = new DirectRouter(service);
                        routes.cache(normal, router);
                        return router.route(request, response);
                     }
                  }
               }
               return null;
            }
            return router.route(request, response);
         }
      }
      return null;
   }



}
