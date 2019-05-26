package org.simpleframework.module.resource.container;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.http.socket.service.RouterContainer;
import org.simpleframework.module.resource.Acceptor;
import org.simpleframework.module.resource.ResourceMatcher;
import org.simpleframework.module.resource.SubscriptionRouter;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class ContainerAcceptor implements Acceptor {
   
   private final ContainerRequestHandler container;
   private final RouterContainer wrapper;
   private final SocketProcessor server;
   private final Connection connection;

   public ContainerAcceptor(ResourceMatcher matcher, SubscriptionRouter router, String name, String cookie, int threads) throws IOException {
      this.container = new ContainerRequestHandler(matcher, name, cookie);
      this.wrapper = new RouterContainer(container, router, threads);
      this.server = new ContainerSocketProcessor(wrapper);
      this.connection = new SocketConnection(server);
   }
   
   @Override
   public InetSocketAddress bind(int port, SSLContext context) {
      InetSocketAddress listen = new InetSocketAddress(port);
      
      try {   
         return (InetSocketAddress)connection.connect(listen, context);
      } catch(Exception e) {
         throw new IllegalStateException("Could not start server on port " + port, e);
      }
   }
}
