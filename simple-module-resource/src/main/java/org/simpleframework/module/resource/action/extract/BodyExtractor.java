package org.simpleframework.module.resource.action.extract;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

import org.simpleframework.http.Request;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.Model;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.resource.annotation.Body;

public class BodyExtractor implements Extractor<Object> {

   @Override
   public Object extract(Argument argument, Context context) throws Exception {
      Body annotation = argument.getAnnotation(Body.class);
      
      if(annotation != null) {
         Class type = argument.getType();
         Model model = context.getModel();
         Request request = model.get(Request.class);
         
         if(request == null) {
            throw new IllegalStateException("Could not get request from model");
         }
         if (type == InputStream.class) {
            return request.getInputStream();
         }
         if (type == ReadableByteChannel.class) {
            return request.getByteChannel();
         }
         if (type == String.class) {
            return request.getContent();
         }
      }
      return null;
   }

   @Override
   public boolean accept(Argument argument) {
      Body annotation = argument.getAnnotation(Body.class);
      
      if(annotation != null) {
         Class type = argument.getType();
         
         if (type == InputStream.class) {
            return true;
         }
         if (type == ReadableByteChannel.class) {
            return true;
         }
         if (type == String.class) {
            return true;
         }
      }
      return false;
   }
}
