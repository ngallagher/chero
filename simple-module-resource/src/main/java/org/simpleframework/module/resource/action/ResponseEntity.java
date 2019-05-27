package org.simpleframework.module.resource.action;

import static org.simpleframework.http.Protocol.CONTENT_TYPE;

import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Status;
import org.simpleframework.http.message.Message;
import org.simpleframework.http.message.MessageHeader;
import org.simpleframework.module.resource.MediaType;

public abstract class ResponseEntity {
   
   public abstract Message getHeader();
   public abstract Object getEntity();
   public abstract Status getStatus();

   public static ResponseBuilder create(Status status) {
      return new ResponseEntityBuilder(status);
   }
   
   private static class ResponseResult extends ResponseEntity {
      
      private final AtomicReference<Object> entity;
      private final Message header;
      private final Status status;
      
      public ResponseResult(AtomicReference<Object> entity, Status status, Message header) {
         this.header = header;
         this.status = status;
         this.entity = entity;
      }
      
      @Override
      public Object getEntity() {
         return entity.get();
      }      
      
      @Override
      public Message getHeader() {
         return header;
      }

      @Override
      public Status getStatus() {
         return status;
      }
      
   }
   
   private static class ResponseEntityBuilder implements ResponseBuilder {
      
      private final AtomicReference<Object> entity;
      private final ResponseResult result;
      private final MessageHeader header;
      
      public ResponseEntityBuilder(Status status) {
         this.entity = new AtomicReference<>();
         this.header = new MessageHeader();
         this.result = new ResponseResult(entity, status, header);
      }

      @Override
      public ResponseBuilder header(String name, String value) {
         header.setValue(name, value);
         return this;
      }

      @Override
      public ResponseBuilder cookie(String name, String value) {
         header.setCookie(name, value);
         return this;
      }

      @Override
      public ResponseBuilder coookie(Cookie cookie) {
         header.setCookie(cookie);
         return this;
      }

      @Override
      public ResponseBuilder type(String type) {
         header.setValue(CONTENT_TYPE, type);
         return this;
      }
      
      @Override
      public ResponseBuilder type(MediaType type) {
         header.setValue(CONTENT_TYPE, type.value);
         return this;
      }

      @Override
      public ResponseBuilder entity(Object value) {
         entity.set(value);
         return this;
      }

      @Override
      public ResponseEntity create() {
         return result;
      }
      
   }
}
