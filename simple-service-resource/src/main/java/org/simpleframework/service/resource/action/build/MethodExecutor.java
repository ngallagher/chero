package org.simpleframework.service.resource.action.build;

import static java.lang.Integer.MIN_VALUE;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.service.build.extract.ParameterBuilder;
import org.simpleframework.service.build.validate.Validator;
import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.Model;
import org.simpleframework.service.context.Validation;

public class MethodExecutor {

   private final ParameterBuilder extractor;
   private final PathResolver resolver;
   private final MethodMatcher matcher;
   private final MethodHeader header;
   private final Validator validator;
   private final Method method;

   public MethodExecutor(MethodMatcher matcher, MethodHeader header, ParameterBuilder extractor, Validator validator, Method method) {
      this.resolver = new PathResolver();
      this.extractor = extractor;
      this.validator = validator;
      this.matcher = matcher;
      this.header = header;
      this.method = method;
   }

   public Object execute(Object value, Context context) throws Exception {
      try {
         evaluate(value, context);

         if (valid(context)) {
            return invoke(value, context);               
         }
      } catch (Throwable cause) {
         context.setError(cause);
         return cause;
      }
      return null;
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
            return extractor.score(context);
         }
      }
      return MIN_VALUE;
   }

   private Object invoke(Object value, Context context) throws Exception {
      Object[] arguments = extractor.extract(context);
      Object result = method.invoke(value, arguments);

      if (result == null) {
         Model model = context.getModel();
         Response response = model.get(Response.class);
         
         if(response == null) {
            throw new IllegalStateException("Could not get response from model");
         }
      }
      return result;
   }

   private void evaluate(Object value, Context context) throws Exception {
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
         header.applyHeader(context);
      }
   }

   private boolean valid(Context context) throws Exception {
      Object[] arguments = extractor.extract(context);
      Validation validation = context.getValidation();

      for (int i = 0; i < arguments.length; i++) {
         Set<String> violations = validator.validateParameter(method, arguments[i], i);

         for (String violation : violations) {
            validation.addError(violation);
         }
      }
      return validation.isValid();
   }

   @Override
   public String toString() {
      return method.toString();
   }
}
