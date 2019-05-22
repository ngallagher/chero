package org.simpleframework.service.resource.action.extract;

import org.simpleframework.http.Part;
import org.simpleframework.http.Request;
import org.simpleframework.service.build.extract.Extractor;
import org.simpleframework.service.build.extract.Parameter;
import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.Model;
import org.simpleframework.service.resource.annotation.PartParam;

public class PartExtractor implements Extractor<Object> {

   @Override
   public Object extract(Parameter parameter, Context context) throws Exception {
      PartParam annotation = parameter.getAnnotation(PartParam.class);
      
      if(annotation != null) {
         Class type = parameter.getType();
         Model model = context.getModel();
         Request request = model.get(Request.class);
         
         if(request == null) {
            throw new IllegalStateException("Could not get request from model");
         }
         String name = annotation.value();
         String substitute = parameter.getDefault();
   
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
   public boolean accept(Parameter parameter) throws Exception {
      PartParam annotation = parameter.getAnnotation(PartParam.class);
      
      if(annotation != null) {
         String name = annotation.value();
         Class type = parameter.getType();
   
         if (type == Part.class) {
            return name != null;
         }
      }
      return false;
   }
}
