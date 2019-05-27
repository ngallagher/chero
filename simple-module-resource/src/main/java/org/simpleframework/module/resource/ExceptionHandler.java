package org.simpleframework.module.resource;

import static org.simpleframework.http.Status.INTERNAL_SERVER_ERROR;
import static org.simpleframework.module.resource.MediaType.TEXT_PLAIN;
import static org.simpleframework.module.resource.ResourceEvent.ERROR;

import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.trace.Trace;

public class ExceptionHandler {

   public void handle(Request request, Response response, Throwable cause) {
      Channel channel = request.getChannel();
      Trace trace = channel.getTrace();
      
      try {
         PrintStream output = response.getPrintStream();
         
         response.setStatus(INTERNAL_SERVER_ERROR);
         response.setContentType(TEXT_PLAIN.value);
         cause.printStackTrace(output);
         cause.printStackTrace();
         response.close();
      } catch(Exception ignore) {
         trace.trace(ERROR, ignore);
      }
      
   }
}
