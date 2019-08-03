package org.simpleframework.resource.write;

import java.util.concurrent.CompletableFuture;

import org.simpleframework.http.Response;

public class CompletableFutureWriter implements BodyWriter<CompletableFuture>  {
   
   private final ResponseWriter writer;
   
   public CompletableFutureWriter(ResponseWriter writer) {
      this.writer = writer;
   }

   @Override
   public boolean accept(Response response, Object result) throws Exception {
      if (result != null) {
         return CompletableFuture.class.isInstance(result);
      }
      return false;
   }

   @Override
   public boolean write(Response response, CompletableFuture result) throws Exception {
      result.thenAccept(entity -> {
         try {
            if(writer.write(response, entity)) {
               response.close();
            }
         } catch(Exception e) {           
         }
      });
      return false;
   }

}
