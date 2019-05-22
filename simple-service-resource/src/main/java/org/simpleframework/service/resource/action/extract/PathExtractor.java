package org.simpleframework.service.resource.action.extract;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.service.build.extract.Parameter;
import org.simpleframework.service.build.extract.StringConverterExtractor;
import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.Model;
import org.simpleframework.service.resource.annotation.PathParam;

public class PathExtractor extends StringConverterExtractor {

   public PathExtractor() {
      super(PathParam.class);
   }
   
   @Override
   protected List<String> resolve(Parameter parameter, Context context) {
      PathParam annotation = parameter.getAnnotation(PathParam.class);
      
      if(annotation != null) {
         Model model = context.getModel();
         Request request = model.get(Request.class);
         
         if(request == null) {
            throw new IllegalStateException("Could not get request from model");
         }
         String name = annotation.value();
         Object value = request.getAttribute(name);
         
         if (value != null) {
            Class actual = value.getClass();
   
            if (actual == String[].class) {
               return Arrays.asList((String[])value);
            }
            if (actual == String.class) {
               return Arrays.asList((String)value);
            }
         }
      }
      return null;
   }
}
