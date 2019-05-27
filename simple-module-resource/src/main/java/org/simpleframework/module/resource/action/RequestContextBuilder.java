package org.simpleframework.module.resource.action;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.MapContext;

public class RequestContextBuilder {

   private final Variable[] variables;
   
   public RequestContextBuilder() {
      this.variables = Variable.values();
   }

   public Context build(Request request, Response response) throws Exception {
      Context context = new MapContext();

      for (Variable variable : variables) {
         variable.update(request, response, context);
      }
      return context;
   }
}
