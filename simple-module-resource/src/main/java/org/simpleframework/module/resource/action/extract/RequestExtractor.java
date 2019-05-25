package org.simpleframework.module.resource.action.extract;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.transport.Channel;

public class RequestExtractor implements Extractor<Object> {

   @Override
   public Object extract(Argument argument, Context context) throws Exception {
      Class type = argument.getType();
      Model model = context.getModel();
      Request request = model.get(Request.class);
      
      if(request == null) {
         throw new IllegalStateException("Could not get request from model");
      }
      if (type == Request.class) {
         return request;
      }
      if (type == Query.class) {
         return request.getQuery();
      }
      if (type == Channel.class) {
         return request.getChannel();
      }
      if (type == InputStream.class) {
         return request.getInputStream();
      }
      if (type == ReadableByteChannel.class) {
         return request.getByteChannel();
      }
      return null;
   }

   @Override
   public boolean accept(Argument argument) {
      Class type = argument.getType();
      
      if (type == Request.class) {
         return true;
      }
      if (type == Query.class) {
         return true;
      }
      if (type == Channel.class) {
         return true;
      }
      if (type == InputStream.class) {
         return true;
      }
      if (type == ReadableByteChannel.class) {
         return true;
      }
      return false;
   }
}
