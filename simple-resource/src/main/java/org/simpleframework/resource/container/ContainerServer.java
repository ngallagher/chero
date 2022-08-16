package org.simpleframework.resource.container;

import org.simpleframework.module.service.ServiceBinder;
import org.simpleframework.resource.ResourceManager;
import org.simpleframework.resource.ResourceMatcher;
import org.simpleframework.resource.SubscriptionRouter;

class ContainerServer {
   
   private final SubscriptionRouter router;
   private final ResourceManager assembler;
   private final ServiceBinder binder;

   public ContainerServer(ServiceBinder binder, ResourceManager assembler, SubscriptionRouter router) {
      this.assembler = assembler;
      this.binder = binder;
      this.router = router;
   }

   public Acceptor start(Logger logger, String name, String cookie, int threads) {
      ResourceMatcher matcher = assembler.create();

      try {
         return new ConnectionAcceptor(matcher, router, binder::stop, logger, name, cookie, threads);
      } catch(Exception e) {
         throw new IllegalStateException("Could not start server", e);
      }
   }
}
