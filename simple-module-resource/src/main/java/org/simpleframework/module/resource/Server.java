package org.simpleframework.module.resource;

public abstract class Server {
   
   private String name;
   private String cookie;
   private int threads;  
   
   protected Server() {
      this(SessionCookie.SESSION_ID);
   }

   protected Server(String cookie) {
      this(cookie, 10);
   }
   
   protected Server(String cookie, int threads) {
      this.name = Server.class.getSimpleName();
      this.threads = threads;
      this.cookie = cookie;
   }
   
   public Server withName(String name) {
      this.name = name;
      return this;
   }
   
   public Server withCookie(String cookie) {
      this.cookie = cookie;
      return this;
   }
   
   public Server withThreads(int threads) {
      this.threads = threads;
      return this;
   }
   
   public Acceptor start() {
      if(name == null) {
         throw new IllegalArgumentException("Server requires a name");
      }
      if(cookie == null) {
         throw new IllegalArgumentException("Server requires a session cookie");
      }
      if(threads <= 0) {
         throw new IllegalArgumentException("Server must have at least one thread");
      }
      return start(name, cookie, threads);
   }
   
   protected abstract Acceptor start(String name, String cookie, int threads);
}
