package org.simpleframework.module.resource.server;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

public interface Acceptor {
   
   default InetSocketAddress bind() {
      return bind(0, null);
   }

   default InetSocketAddress bind(SSLContext context) {
      return bind(0, context);
   }

   default InetSocketAddress bind(int port) {
      return bind(port, null);
   }

   InetSocketAddress bind(int port, SSLContext context);
}
