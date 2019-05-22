package org.simpleframework.service.resource.server;

import java.net.InetSocketAddress;

public class ResourceServer {
   
   private final ResourceServerBuilder builder;
   private final int port;
   
   public ResourceServer(ResourceServerBuilder builder, int port) {
      this.builder = builder;
      this.port = port;
   }

   public InetSocketAddress start() {
      try {
         return builder.create(null, port);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create container", e);
      }
   }
}
