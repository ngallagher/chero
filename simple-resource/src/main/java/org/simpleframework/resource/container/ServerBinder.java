package org.simpleframework.resource.container;

abstract class ServerBinder implements Server {

   private Logger logger;
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
      this.logger = (request) -> {};
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

   public Server log(Logger logger) {
      this.logger = logger;
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
      if(logger == null) {
         throw new IllegalArgumentException("Server must have a logger");
      }
      return start(logger, name, cookie, threads);
   }
   
   protected abstract Acceptor start(Logger logger, String name, String cookie, int threads);
}