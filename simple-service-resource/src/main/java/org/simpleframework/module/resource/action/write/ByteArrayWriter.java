package org.simpleframework.module.resource.action.write;

import java.io.OutputStream;

import org.simpleframework.http.Response;

public class ByteArrayWriter implements BodyWriter<byte[]> {

   @Override
   public boolean accept(Response response, Object result) throws Exception {
      if (result != null) {
         return byte[].class.isInstance(result);
      }
      return false;
   }

   @Override
   public void write(Response response, byte[] result) throws Exception {
      OutputStream output = response.getOutputStream();
      
      if(result.length > 0) {
         response.setContentLength(result.length);
         output.write(result);
      }
   }
}
