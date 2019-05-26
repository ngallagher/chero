package org.simpleframework.module.resource.container;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

import org.simpleframework.module.resource.ResourceSystem;
import org.simpleframework.module.resource.Server;
import org.simpleframework.module.resource.SubscriptionRouter;

public class ResourceServer implements Server {
   
   private final ResourceServerAcceptor acceptor;

   public ResourceServer(ResourceSystem system, SubscriptionRouter router) throws IOException {
      this.acceptor = new ResourceServerAcceptor(system, router);
   }

   @Override
   public InetSocketAddress start(int port, SSLContext context) {
      return acceptor.accept(port, context);
   }
}
