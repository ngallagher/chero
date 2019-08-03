package org.simpleframework.resource.write;

import java.io.PrintStream;

import org.simpleframework.http.Response;

public class ObjectWriter implements BodyWriter<Object> {

   @Override
   public boolean accept(Response response, Object result) throws Exception {
      return result != null;
   }

   @Override
   public boolean write(Response response, Object result) throws Exception {
      PrintStream output = response.getPrintStream();

      if(result != null) {
         output.println(result);
      }
      return true;
   }

}
