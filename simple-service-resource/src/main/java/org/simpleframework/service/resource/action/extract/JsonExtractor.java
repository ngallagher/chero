package org.simpleframework.service.resource.action.extract;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.service.build.extract.Extractor;
import org.simpleframework.service.build.extract.Parameter;
import org.simpleframework.service.build.extract.StringConverter;
import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.Model;
import org.simpleframework.service.resource.annotation.Body;

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
   public Object extract(Parameter parameter, Context context) throws Exception {
      Body annotation = parameter.getAnnotation(Body.class);
      
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
            Class require = parameter.getType();
            
            return gson.fromJson(body, require);
         }
      }
      return null;
   }

   @Override
   public boolean accept(Parameter parameter) throws Exception {
      Body annotation = parameter.getAnnotation(Body.class);
      
      if(annotation != null) {
         Class require = parameter.getType();
         
         if(!converter.accept(require)) {
            return !extractor.accept(parameter);
         }
      }
      return false;
   }
}
