package org.simpleframework.resource.extract;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.extract.StringConverterExtractor;
import org.simpleframework.resource.annotation.PathParam;

public class PathExtractor extends StringConverterExtractor {

   public PathExtractor() {
      super(PathParam.class);
   }

   @Override
   public float score(Argument argument, Context context) {
      PathParam annotation = argument.getAnnotation(PathParam.class);

      if(annotation != null) {
         try {
            return extract(argument, context) == null ? -1 : 2;
         } catch (Exception e) {
            return -1;
         }
      }
      return 0;
   }
   
   @Override
   protected List<String> resolve(Argument argument, Context context) {
      PathParam annotation = argument.getAnnotation(PathParam.class);
      
      if(annotation != null) {
         Model model = context.getModel();
         String name = annotation.value();
         Object value = model.get(name);
         
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
