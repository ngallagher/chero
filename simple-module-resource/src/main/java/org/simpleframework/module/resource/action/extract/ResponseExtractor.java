package org.simpleframework.module.resource.action.extract;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.channels.WritableByteChannel;

import org.simpleframework.http.Response;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.extract.Extractor;

public class ResponseExtractor implements Extractor<Object> {

   @Override
   public Object extract(Argument argument, Context context) throws Exception {
      Class type = argument.getType();
      Model model = context.getModel();
      Response response = model.get(Response.class);
      
      if (type == Response.class) {
         return response;
      }
      if (type == OutputStream.class) {
         return response.getOutputStream();
      }
      if (type == PrintStream.class) {
         return response.getOutputStream();
      }
      if (type == WritableByteChannel.class) {
         return response.getByteChannel();
      }
      return response;
   }

   @Override
   public boolean accept(Argument argument) {
      Class type = argument.getType();

      if (type == Response.class) {
         return true;
      }
      if (type == OutputStream.class) {
         return true;
      }
      if (type == PrintStream.class) {
         return true;
      }
      if (type == WritableByteChannel.class) {
         return true;
      }
      return false;
   }
}
