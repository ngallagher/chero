package org.simpleframework.resource.write;

import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.message.Message;
import org.simpleframework.resource.action.ResponseEntity;

public class ResponseEntityWriter implements BodyWriter<ResponseEntity> {
   
   private final ResponseWriter writer;
   
   public ResponseEntityWriter(ResponseWriter writer) {
      this.writer = writer;
   }

   @Override
   public boolean accept(Response response, Object result) throws Exception {
      if (result != null) {
         return ResponseEntity.class.isInstance(result);
      }
      return false;
   }

   @Override
   public boolean write(Response response, ResponseEntity entity) throws Exception {
      Message header = entity.getHeader();
      Status status = entity.getStatus();
      Object body = entity.getEntity();
      
      header.getNames().forEach(name -> {
         String value = header.getValue(name);
         response.setValue(name, value);         
      });
      header.getCookies().forEach(cookie -> {
         response.setCookie(cookie);
      });
      response.setStatus(status);
      return writer.write(response, body);
   }
}
