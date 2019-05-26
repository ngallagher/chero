package org.simpleframework.module.resource.container;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.http.socket.service.RouterContainer;
import org.simpleframework.module.resource.ResourceMatcher;
import org.simpleframework.module.resource.SubscriptionRouter;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

class ConnectionAcceptor implements Acceptor {
   
   private final ServerContainer container;
   private final RouterContainer wrapper;
   private final SocketProcessor server;  
   private final Connection connection;

   public ConnectionAcceptor(ResourceMatcher matcher, SubscriptionRouter router, String name, String session) throws IOException {
      this(matcher, router, name, session, 10);
   }
   
   public ConnectionAcceptor(ResourceMatcher matcher, SubscriptionRouter router, String name, String session, int threads) throws IOException {
      this.container = new ServerContainer(matcher, name, session);
      this.wrapper = new RouterContainer(container, router, threads);
      this.server = new ContainerSocketProcessor(wrapper);
      this.connection = new SocketConnection(server);
   }
   
   @Override
   public InetSocketAddress bind(String host, int port, SSLContext context) {
      InetSocketAddress listen = new InetSocketAddress(host, port);
      
      try {   
         return (InetSocketAddress)connection.connect(listen, context);
      } catch(Exception e) {
         throw new IllegalStateException("Could not listen on port " + port, e);
      }
   }
   
   @Override
   public void close() {
      try {
         connection.close();
      } catch(Exception e) {
         throw new IllegalStateException("Could not stop listener", e);
      }
   }

}
