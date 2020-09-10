package org.simpleframework.resource.build;

import java.util.Map;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;

public class MethodExecutor {

   private final ScoreEvaluator evaluator;
   private final PathResolver resolver;
   private final MethodMatcher matcher;
   private final MethodHeader header;
   private final Function function;

   public MethodExecutor(MethodMatcher matcher, MethodHeader header, Function function) {
      this.evaluator = new ScoreEvaluator(matcher, header, function);
      this.resolver = new PathResolver();
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

   private Object evaluate(Context context) throws Exception {
      Model model = context.getModel();
      Request request = model.get(Request.class);
      Response response = model.get(Response.class);

      if (request == null || response == null) {
         throw new IllegalStateException("Could not get request or response from model");
      }
      String normalized = resolver.resolve(context);
      Map<String, String> attributes = matcher.evaluate(normalized);

      if (!attributes.isEmpty()) {
         attributes.forEach(model::set);
      }
      if (!response.isCommitted()) {
         header.apply(context);
      }
      return function.getValue(context);
   }

   public float score(Context context) throws Exception {
      return evaluator.score(context);
   }

   @Override
   public String toString() {
      return String.valueOf(function);
   }
}
