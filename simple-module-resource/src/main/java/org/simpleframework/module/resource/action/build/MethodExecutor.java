package org.simpleframework.module.resource.action.build;

import static java.lang.Integer.MIN_VALUE;

import java.util.Map;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;

public class MethodExecutor {

   private final PathResolver resolver;
   private final MethodMatcher matcher;
   private final MethodHeader header;
   private final Function function;
   
   public MethodExecutor(MethodMatcher matcher, MethodHeader header, Function function) {
      this.resolver = new PathResolver();
      this.function = function;
      this.matcher = matcher;
      this.header = header;
   }

   public Object execute(Context context) throws Exception {
      try {
         evaluate(context);
         return function.getValue(context);               
      } catch (Throwable cause) {
         context.setError(cause);
         return cause;
      }
   }

   public float score(Context context) throws Exception {
      Model model = context.getModel();
      Request request = model.get(Request.class);
      
      if(request == null) {
         throw new IllegalStateException("Could not get request from model");
      }
      String method = request.getMethod();
      String verb = matcher.verb();
      
      if(method.equalsIgnoreCase(verb)) {
         Path path = request.getPath();
         String normal = path.getPath();
         String ignore = matcher.ignore();
         
         if(ignore.isEmpty() || !normal.matches(ignore)) { 
            float score = header.score(context);
            
            if(score > 0f) {
               return function.getScore(context);
            }
         }
      }
      return MIN_VALUE;
   }

   private void evaluate(Context context) throws Exception {
      Model model = context.getModel();
      Request request = model.get(Request.class);
      Response response = model.get(Response.class);
      
      if(request == null || response == null) {
         throw new IllegalStateException("Could not get request or response from model");
      }
      String normalized = resolver.resolve(context);
      Map<String, String> parameters = matcher.evaluate(normalized);
      Map attributes = request.getAttributes();

      if (!parameters.isEmpty()) {
         attributes.putAll(parameters);
      }
      if (!response.isCommitted()) {
         header.apply(context);
      }
   }
   
   @Override
   public String toString() {
      return String.valueOf(function);
   }
}
