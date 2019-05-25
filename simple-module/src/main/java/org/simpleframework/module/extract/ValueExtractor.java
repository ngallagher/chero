package org.simpleframework.module.extract;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.module.annotation.Value;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.context.Context;

public class ValueExtractor extends StringConverterExtractor {

   public ValueExtractor() {
      super(Value.class);
   }
   
   @Override
   protected List<String> resolve(Argument argument, Context context) {
      Value annotation = argument.getAnnotation(Value.class);
      
      if(annotation != null) {
         String name = annotation.value();
         int length = name.length();
         
         if(length > 0) {
            if(name.startsWith("${") && name.endsWith("}")) {
               String substitute = argument.getDefault();
               String token = name.substring(2, length - 1);
               String value = System.getProperty(token, substitute);
               
               if(value != null) {
                  return Arrays.asList(value);
               }
            }
            return Arrays.asList(name);
         }
      }
      return null;
   }
}