package org.simpleframework.resource.container;

abstract class ServerBinder implements Server {
   
   private String name;
   private String cookie;
   private int threads;  
   
   protected ServerBinder() {
      this("SESSID");
   }

   protected ServerBinder(String cookie) {
      this(cookie, 10);
   }
   
   protected ServerBinder(String cookie, int threads) {
      this.name = Server.class.getSimpleName();
      this.threads = threads;
      this.cookie = cookie;
   }
   
   @Override
   public Server name(String name) {
      this.name = name;
      return this;
   }
   
   @Override
   public Server session(String cookie) {
      this.cookie = cookie;
      return this;
   }
   
   @Override
   public Server threads(int threads) {
      this.threads = threads;
      return this;
   }
   
   @Override
   public Acceptor start() {
      if(name == null) {
         throw new IllegalArgumentException("Server requires a name");
      }
      if(threads <= 0) {
         throw new IllegalArgumentException("Server must have at least one thread");
      }
      return start(name, cookie, threads);
   }
   
   protected abstract Acceptor start(String name, String cookie, int threads);
}