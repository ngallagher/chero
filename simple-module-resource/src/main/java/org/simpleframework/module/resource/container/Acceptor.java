package org.simpleframework.module.resource.container;

import java.io.Closeable;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

public interface Acceptor extends Closeable {
   
   default InetSocketAddress bind() {
      return bind(0);
   }
   
   default InetSocketAddress bind(String host) {
      return bind(host, 0, null);
   }

   default InetSocketAddress bind(SSLContext context) {
      return bind(0, context);
   }
   
   default InetSocketAddress bind(String host, SSLContext context) {
      return bind(host, 0, context);
   }
   
   default InetSocketAddress bind(int port) {
      return bind(port, null);
   }

   default InetSocketAddress bind(int port, SSLContext context) {
      return bind("0.0.0.0", port, context);
   }   

   default InetSocketAddress bind(String host, int port) {
      return bind(host, port, null);
   }
   
   InetSocketAddress bind(String host, int port, SSLContext context);
}
