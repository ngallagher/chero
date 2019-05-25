package org.simpleframework.module.resource.action.extract;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.Model;
import org.simpleframework.module.extract.StringConverterExtractor;
import org.simpleframework.module.resource.annotation.PathParam;

public class PathExtractor extends StringConverterExtractor {

   public PathExtractor() {
      super(PathParam.class);
   }
   
   @Override
   protected List<String> resolve(Argument argument, Context context) {
      PathParam annotation = argument.getAnnotation(PathParam.class);
      
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
