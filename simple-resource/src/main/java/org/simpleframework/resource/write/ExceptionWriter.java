package org.simpleframework.resource.write;

import static org.simpleframework.http.Status.INTERNAL_SERVER_ERROR;

import java.io.PrintStream;

import org.simpleframework.http.Response;

public class ExceptionWriter implements BodyWriter<Throwable> {

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
         response.setStatus(INTERNAL_SERVER_ERROR);
         cause.printStackTrace(output);
      }
      return true;
   }
}
