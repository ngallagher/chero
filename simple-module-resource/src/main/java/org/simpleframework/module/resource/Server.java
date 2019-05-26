package org.simpleframework.module.resource;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

public interface Server {
   
   default InetSocketAddress start() {
      return start(0, null);
   }

   default InetSocketAddress start(SSLContext context) {
      return start(0, context);
   }

   default InetSocketAddress start(int port) {
      return start(port, null);
   }

   InetSocketAddress start(int port, SSLContext context);
}
