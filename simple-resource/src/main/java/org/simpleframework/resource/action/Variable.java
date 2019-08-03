package org.simpleframework.resource.action;

import java.util.Map;

import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.core.Validation;

public enum Variable {
   REQUEST(Request.class, "request") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return request;
      }
   },
   RESPONSE(Response.class, "response") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return response;
      }
   },
   PARAMETERS(Query.class, "parameters") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return request.getQuery();
      }
   },
   ATTRIBUTES(Map.class, "attributes") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return request.getAttributes();
      }
   },
   MESSAGE(String.class, "message") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return request.getContent();
      }
   },
   VALIDATION(Validation.class, "validation") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return context.getValidation();
      }
   },
   ERROR(Error.class, "error") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return context.getError();
      }
   };

   private final String name;
   private final Class type;
   
   private Variable(Class type, String name) {
      this.type = type;
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void update(Request request, Response response, Context context) throws Exception {
      Object value = extract(request, response, context);
      Model model = context.getModel();

      if (value != null) {
         model.set(name, value);
         model.set(type, value);
      }
   }

   public abstract Object extract(Request request, Response response, Context context) throws Exception;
}
