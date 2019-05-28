package org.simpleframework.module.resource.action.demo;

import java.util.concurrent.CompletableFuture;

import org.simpleframework.http.Status;
import org.simpleframework.module.Application;
import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.annotation.Value;
import org.simpleframework.module.resource.MediaType;
import org.simpleframework.module.resource.action.ResponseEntity;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.annotation.PathParam;
import org.simpleframework.module.resource.annotation.Produces;
import org.simpleframework.module.resource.container.ServerDriver;

@Module
public class DemoApplication {
   
   @Component
   public static class DemoService {
      
      @Value("${message}")
      private String text;
      
      public String message() {
         return text + " " + this;
      }
   }

   @Path
   public static class DemoResource {
      
      @Inject
      private DemoService service;
      
      @GET
      @Path("/.*")
      @Produces("text/plain")
      public CompletableFuture<ResponseEntity> helloWorld() {
         return CompletableFuture.supplyAsync(() -> ResponseEntity.create(Status.OK)
            .type(MediaType.TEXT_PLAIN)
            .cookie("TEST", "123")
            .entity(service.message())
            .create()
         );
      }
      
      @GET
      @Path("/bye/{name}")
      @Produces("text/plain")
      public CompletableFuture<ResponseEntity> goodBye(@PathParam("name") String name) {
         return CompletableFuture.supplyAsync(() -> ResponseEntity.create(Status.OK)
            .type(MediaType.TEXT_PLAIN)
            .cookie("TEST", "123")
            .entity("bye " + name)
            .create()
         );
      }
   }
   
   public static void main(String[] list) throws Exception {
      Application.create(ServerDriver.class)
         .path("..")
         .module(DemoApplication.class)
         .create("--message=hi")
         .name("Apache/2.2.14")
         .session("SESSIONID")
         .threads(10)
         .start()
         .bind(8787);
   }
}
