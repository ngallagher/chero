package org.simpleframework.module.resource.action.demo;

import java.util.concurrent.CompletableFuture;

import org.simpleframework.http.Status;
import org.simpleframework.module.Application;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.annotation.Value;
import org.simpleframework.module.resource.action.ResponseEntity;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.annotation.Produces;
import org.simpleframework.module.resource.container.ServerDriver;

@Module
public class DemoApplication {

   @Path
   public static class DemoResource {
      
      @Value("${message}")
      private String text;
      
      @GET
      @Path("/.*")
      @Produces("text/plain")
      public CompletableFuture<ResponseEntity> helloWorld() {
         return CompletableFuture.supplyAsync(() -> ResponseEntity.create(Status.OK)
            .type("text/plain")
            .cookie("TEST", "123")
            .entity(text)
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
