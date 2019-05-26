package org.simpleframework.module.resource.action.write;

import java.util.Collections;
import java.util.List;

import org.simpleframework.http.Response;

public class ResponseWriter {

   private final List<BodyWriter> builders;

   public ResponseWriter() {
      this(Collections.EMPTY_LIST);
   }

   public ResponseWriter(List<BodyWriter> builders) {
      this.builders = builders;
   }

   public boolean write(Response response, Object result) throws Exception {
      for (BodyWriter builder : builders) {
         if (builder.accept(response, result)) {
            return builder.write(response, result);            
         }
      }      
      return true;
   }
}
