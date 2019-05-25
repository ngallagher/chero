package org.simpleframework.module.resource.action.extract;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.StringConverter;
import org.simpleframework.module.resource.annotation.Body;

import com.google.gson.Gson;

public class JsonExtractor implements Extractor<Object> {
   
   private final StringConverter converter;
   private final Extractor extractor;
   private final Gson gson;
   
   public JsonExtractor() {
      this.converter = new StringConverter();
      this.extractor = new BodyExtractor();
      this.gson = new Gson();
   }

   @Override
   public Object extract(Argument argument, Context context) throws Exception {
      Body annotation = argument.getAnnotation(Body.class);
      
      if(annotation != null) {
         Model model = context.getModel();
         Request request = model.get(Request.class);
         Response response = model.get(Response.class);
         
         if(request == null || response == null) {
            throw new IllegalStateException("Could not get request or response from model");
         }
         ContentType type = response.getContentType();
         String value = type.toString();
         
         if(value.equals("application/json")) {
            String body = request.getContent();
            Class require = argument.getType();
            
            return gson.fromJson(body, require);
         }
      }
      return null;
   }

   @Override
   public boolean accept(Argument argument) throws Exception {
      Body annotation = argument.getAnnotation(Body.class);
      
      if(annotation != null) {
         Class require = argument.getType();
         
         if(!converter.accept(require)) {
            return !extractor.accept(argument);
         }
      }
      return false;
   }
}
