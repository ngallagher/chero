package org.simpleframework.module.resource.action.extract;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.module.build.extract.Parameter;
import org.simpleframework.module.build.extract.StringConverterExtractor;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.Model;
import org.simpleframework.module.resource.annotation.AttributeParam;

public class AttributeExtractor extends StringConverterExtractor {
   
   public AttributeExtractor() {
      super(AttributeParam.class);
   }

   @Override
   public List<String> resolve(Parameter parameter, Context context) {
      AttributeParam annotation = parameter.getAnnotation(AttributeParam.class);
      
      if(annotation != null) {
         Model model = context.getModel();
         Request request = model.get(Request.class);
         
         if(request == null) {
            throw new IllegalStateException("Could not get request from model");
         }
         String name = annotation.value();
         String substitute = parameter.getDefault();
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
         if (substitute != null) {
            return Arrays.asList(substitute);
         }
      }
      return null;
   }
}
