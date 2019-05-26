package org.simpleframework.module.resource.action.write;

import java.io.PrintStream;

import org.simpleframework.http.Response;

public class StringWriter implements BodyWriter<String> {

   @Override
   public boolean accept(Response response, Object result) throws Exception {
      if (result != null) {
         return String.class.isInstance(result);
      }
      return false;
   }

   @Override
   public boolean write(Response response, String result) throws Exception {
      PrintStream output = response.getPrintStream();

      if (result != null) {
         output.print(result);
      }
      return true;
   }
}
