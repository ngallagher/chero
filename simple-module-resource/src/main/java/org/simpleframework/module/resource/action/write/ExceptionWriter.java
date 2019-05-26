package org.simpleframework.module.resource.action.write;

import java.io.PrintStream;

import org.simpleframework.http.Response;

public class ExceptionWriter<T extends Throwable> implements BodyWriter<T> {

   @Override
   public boolean accept(Response response, Object result) throws Exception {
      if (result != null) {
         return Throwable.class.isInstance(result);
      }
      return false;
   }

   @Override
   public boolean write(Response response, Throwable cause) throws Exception {
      PrintStream output = response.getPrintStream();

      if(cause != null) {
         cause.printStackTrace(output);
      }
      return true;
   }
}
