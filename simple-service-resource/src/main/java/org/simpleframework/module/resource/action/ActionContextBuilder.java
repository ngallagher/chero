package org.simpleframework.module.resource.action;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.MapContext;

public class ActionContextBuilder {

   private final Variable[] variables;
   
   public ActionContextBuilder() {
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
