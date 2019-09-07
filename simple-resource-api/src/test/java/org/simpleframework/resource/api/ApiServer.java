package org.simpleframework.resource.api;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.simpleframework.http.Status;
import org.simpleframework.module.Application;
import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.annotation.Value;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.resource.MediaType;
import org.simpleframework.resource.action.ResponseEntity;
import org.simpleframework.resource.action.Schema;
import org.simpleframework.resource.annotation.Body;
import org.simpleframework.resource.annotation.Consumes;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.PathParam;
import org.simpleframework.resource.annotation.Produces;
import org.simpleframework.resource.container.Server;
import org.simpleframework.resource.container.ServerDriver;

@Module
public class ApiServer {
   
   @Provides
   public ApiService service(ApiBlah blah, @Value("${message}") String text) {
      return new ApiService();
   }

   @Component
   public static class ApiBlah {
      private String foo;
      private int port;
      
      public String getFoo() {
         return foo;
      }
      
      public int getPort() {
         return port;
      }
   }
   
   @Component
   public static class ApiOther {
      
      private ApiService service;
      
      public ApiOther(ApiService service) {
         this.service = service;
      }
   }
   
   public static class ApiService {
      
      @Value("${message}")
      private String text;
      
      public String message() {
         return text + " " + this;
      }
   }

   @Path("/some/{id}")
   @Produces("text/css")
   public static class ApiResource {
      
      @Inject
      private ApiService service;
      
      @GET
      @Path("/value")
      @Produces({"application/json", "application/vnd.exchange-v1+json"})
      public ResponseEntity value(@PathParam("id") String id) {
         return ResponseEntity.create(Status.OK)
            .cookie("TEST", "123")
            .entity("{\"id\": 12}")
            .create();
      }
      
      @GET
      @Path("/delay")
      public CompletableFuture<ResponseEntity> delay(@PathParam("id") String id) {
         return CompletableFuture.supplyAsync(() -> {
            try {
               Thread.sleep(10000);
            }catch(Exception e) {}
            return ResponseEntity.create(Status.OK)
               .type(MediaType.TEXT_PLAIN)
               .cookie("TEST", "123")
               .entity(service.message() + " " + id)
               .create();
         });
      }
      
      @GET
      @Path("/ping")
      public ResponseEntity ping(@PathParam("id") String id) {
         return ResponseEntity.create(Status.OK)
            .type(MediaType.TEXT_PLAIN)
            .cookie("TEST", "123")
            .entity(LocalDateTime.now())
            .create();
      }
      
      @GET
      @Path("/bye/{name}")
      @Produces("text/plain")
      public CompletableFuture<ResponseEntity> goodBye(@PathParam("name") String name) {
         return CompletableFuture.supplyAsync(() -> ResponseEntity.create(Status.OK)
            .cookie("TEST", "123")
            .entity("bye " + name)
            .create()
         );
      }
      
      @GET
      @Path("/post/{name}")
      @Consumes("application/json")
      @Produces("application/json")
      public CompletableFuture<ResponseEntity> post(@Body ApiBlah other) {
         return CompletableFuture.supplyAsync(() -> ResponseEntity.create(Status.OK)
            .cookie("TEST", "123")
            .entity(other)
            .create()
         );
      }
   }
   
   public static void main(String[] list) throws Exception {
      Server server = Application.create(ServerDriver.class)
         .path("..")
         .module(ApiServer.class)
         .create("--message=hi")
         .name("Apache/2.2.14")
         .session("SESSIONID")
         .threads(10);
      
      
      server.start().bind(1992);
      try {
         new URL("http://localhost:1992/some/11/ping").openStream().read();
      }catch(Exception e) {}
      Schema schema = server.resolve(Schema.class);
      ClassPath path = server.resolve(ClassPath.class);
      SchemaGenerator generator = new SchemaGenerator(path, schema);
      System.err.println(generator.generate());
      
   }
}
