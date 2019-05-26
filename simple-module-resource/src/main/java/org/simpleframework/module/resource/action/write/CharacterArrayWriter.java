package org.simpleframework.module.resource.action.write;

import java.io.PrintStream;

import org.simpleframework.http.Response;

public class CharacterArrayWriter implements BodyWriter<char[]> {

   @Override
   public boolean accept(Response response, Object result) throws Exception {
      if (result != null) {
         return char[].class.isInstance(result);
      }
      return false;
   }

   @Override
   public boolean write(Response response, char[] result) throws Exception {
      PrintStream output = response.getPrintStream();

      if(result.length > 0) {
         output.print(result);
      }
      return true;
   }
}
