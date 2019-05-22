package org.simpleframework.service.resource.server;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.http.socket.service.Router;
import org.simpleframework.http.socket.service.RouterContainer;
import org.simpleframework.service.resource.ResourceMatcher;
import org.simpleframework.service.resource.ResourceSystem;
import org.simpleframework.service.resource.SubscriptionRouter;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class ResourceServerBuilder {
   
   private final ResourceMatcher matcher;
   private final Router router;

   public ResourceServerBuilder(ResourceSystem system, SubscriptionRouter router) {
      this.matcher = system.create();
      this.router = router;
   }
   
   public InetSocketAddress create(SSLContext context, int port) throws Exception {
      ResourceServerContainer container = new ResourceServerContainer(matcher);
      RouterContainer wrapper = new RouterContainer(container, router, 5);
      SocketProcessor server = new ContainerSocketProcessor(wrapper);
      Connection connection = new SocketConnection(server);
      InetSocketAddress listen = new InetSocketAddress(port);
      
      return (InetSocketAddress)connection.connect(listen, context);
   }
}
