package org.simpleframework.module.resource.container;

import org.simpleframework.module.resource.ResourceMatcher;
import org.simpleframework.module.resource.SubscriptionRouter;
import org.simpleframework.module.resource.action.ActionAssembler;

class ContainerServer {
   
   private final SubscriptionRouter router;
   private final ResourceMatcher matcher;

   public ContainerServer(ActionAssembler assembler, SubscriptionRouter router) {
      this.matcher = assembler.assemble();
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
