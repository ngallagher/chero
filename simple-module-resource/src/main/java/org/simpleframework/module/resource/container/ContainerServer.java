package org.simpleframework.module.resource.container;

import org.simpleframework.module.resource.ResourceMatcher;
import org.simpleframework.module.resource.ResourceSystem;
import org.simpleframework.module.resource.SubscriptionRouter;

class ContainerServer {
   
   private final SubscriptionRouter router;
   private final ResourceMatcher matcher;

   public ContainerServer(ResourceSystem system, SubscriptionRouter router) {
      this.matcher = system.create();
      this.router = router;
   }

   public Acceptor start(String name, String cookie, int threads) {
      try {
         return new ConnectionAcceptor(matcher, router, name, cookie, threads);
      } catch(Exception e) {
         throw new IllegalStateException("Could not start server", e);
      }
   }
}
