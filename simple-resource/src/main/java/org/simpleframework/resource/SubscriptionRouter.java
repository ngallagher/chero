package org.simpleframework.resource;

import static java.lang.Boolean.TRUE;
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
import org.simpleframework.module.common.CopyOnWriteCache;
import org.simpleframework.module.common.LeastRecentlyUsedCache;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.resource.annotation.Subscribe;

public class SubscriptionRouter implements Router {
   
   private final Cache<String, Boolean> failures;
   private final Cache<String, Router> routes;
   private final ComponentManager manager;
   
   public SubscriptionRouter(ComponentManager manager) {
      this(manager, 1000);
   }
   
   public SubscriptionRouter(ComponentManager manager, int capacity) {
      this.failures = new LeastRecentlyUsedCache<String, Boolean>();
      this.routes = new CopyOnWriteCache<String, Router>();
      this.manager = manager;
   }

   @Override
   public Service route(Request request, Response response) {
      String token = request.getValue(UPGRADE);
      
      if(token != null) {
         if(token.equalsIgnoreCase(WEBSOCKET)) {
            Path path = request.getPath();
            String normal = path.getPath();
            
            if(!failures.contains(normal)) {
               Router router = match(normal);
              
               if(router != null) {
                  return router.route(request, response);
               }
               failures.cache(normal, TRUE);
            }
         }
      }
      return null;
   }

   private Router match(String normal) {
      Router router = routes.fetch(normal);
      
      if(router == null) {
         List<Service> services = manager.resolveAll(Service.class);
         
         for(Service service : services) {
            Class<?> type = service.getClass();
            Subscribe label = type.getAnnotation(Subscribe.class);
            
            if(label != null) {
               String pattern = label.value();
               
               if(normal.matches(pattern)) {
                  router = new DirectRouter(service);
                  routes.cache(normal, router);
                  return router;
               }
            }
         }
      }
      return router;
   }



}
