package org.simpleframework.module.resource.container;

import org.simpleframework.module.resource.ResourceManager;
import org.simpleframework.module.resource.ResourceMatcher;
import org.simpleframework.module.resource.SubscriptionRouter;

class ContainerServer {
   
   private final SubscriptionRouter router;
   private final ResourceManager assembler;

   public ContainerServer(ResourceManager assembler, SubscriptionRouter router) {
      this.assembler = assembler;
      this.router = router;
   }

   public Acceptor start(String name, String cookie, int threads) {
      ResourceMatcher matcher = assembler.create();
      
      try {
         return new ConnectionAcceptor(matcher, router, name, cookie, threads);
      } catch(Exception e) {
         throw new IllegalStateException("Could not start server", e);
      }
   }
}
