package org.simpleframework.module.resource.action.extract;

import org.simpleframework.http.Part;
import org.simpleframework.http.Request;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.resource.annotation.PartParam;

public class PartExtractor implements Extractor<Object> {

   @Override
   public Object extract(Argument argument, Context context) throws Exception {
      PartParam annotation = argument.getAnnotation(PartParam.class);
      
      if(annotation != null) {
         Class type = argument.getType();
         Model model = context.getModel();
         Request request = model.get(Request.class);
         
         if(request == null) {
            throw new IllegalStateException("Could not get request from model");
         }
         String name = annotation.value();
         String substitute = argument.getDefault();
   
         if (type == Part.class) {
            return request.getPart(name);
         }
         if (type == String.class) {
            Part part = request.getPart(name);
   
            if (part != null) {
               return part.getContent();
            }
            return substitute;
         }
      }
      return null;
   }

   @Override
   public boolean accept(Argument argument) throws Exception {
      PartParam annotation = argument.getAnnotation(PartParam.class);
      
      if(annotation != null) {
         String name = annotation.value();
         Class type = argument.getType();
   
         if (type == Part.class) {
            return name != null;
         }
      }
      return false;
   }
}
