package org.simpleframework.module.resource.container;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.http.socket.service.RouterContainer;
import org.simpleframework.module.resource.ResourceMatcher;
import org.simpleframework.module.resource.ResourceSystem;
import org.simpleframework.module.resource.SubscriptionRouter;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class ResourceServerAcceptor {
   
   private final ResourceServerContainer container;
   private final ResourceMatcher matcher;
   private final RouterContainer wrapper;
   private final SocketProcessor server;
   private final Connection connection;

   public ResourceServerAcceptor(ResourceSystem system, SubscriptionRouter router) throws IOException {
      this.matcher = system.create();
      this.container = new ResourceServerContainer(matcher);
      this.wrapper = new RouterContainer(container, router, 5);
      this.server = new ContainerSocketProcessor(wrapper);
      this.connection = new SocketConnection(server);
   }
   
   public InetSocketAddress accept(int port, SSLContext context) {
      InetSocketAddress listen = new InetSocketAddress(port);
      
      try {   
         return (InetSocketAddress)connection.connect(listen, context);
      } catch(Exception e) {
         throw new IllegalStateException("Could not start server on port " + port, e);
      }
   }
}
