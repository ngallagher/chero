package org.simpleframework.resource.build;

import static java.lang.Integer.MIN_VALUE;
import static org.simpleframework.module.core.Phase.EXECUTE;
import static org.simpleframework.module.core.Phase.SCORE;

import java.util.Map;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;

public class MethodExecutor {

   private final PathContextBuilder builder;
   private final MethodMatcher matcher;
   private final MethodHeader header;
   private final Function function;
   
   public MethodExecutor(MethodMatcher matcher, MethodHeader header, Function function) {
      this.builder = new PathContextBuilder(matcher);
      this.function = function;
      this.matcher = matcher;
      this.header = header;
   }

   public Object execute(Context context) throws Exception {
      try {
         return evaluate(context);
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
               Context merged = builder.create(context, SCORE); // temporary copy for score
               return function.getScore(merged);
            }
         }
      }
      return MIN_VALUE;
   }

   private Object evaluate(Context context) throws Exception {
      Model model = context.getModel();
      Request request = model.get(Request.class);
      Response response = model.get(Response.class);

      if(request == null || response == null) {
         throw new IllegalStateException("Could not get request or response from model");
      }
      Context merged = builder.create(context, EXECUTE);

      if (!response.isCommitted()) {
         header.apply(merged);
      }
      return function.getValue(merged);
   }
   
   @Override
   public String toString() {
      return String.valueOf(function);
   }
}
