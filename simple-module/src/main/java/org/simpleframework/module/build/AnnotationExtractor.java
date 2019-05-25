package org.simpleframework.module.build;

import java.lang.annotation.Annotation;

import org.simpleframework.module.annotation.DefaultValue;
import org.simpleframework.module.annotation.Required;

public class AnnotationExtractor {

   public AnnotationContext extract(Annotation[] annotations) throws Exception {
      AnnotationContext data = new AnnotationContext();
      
      for (Annotation annotation : annotations) {
         if (annotation instanceof Required) {
            data.setRequired(true);
         } else if (annotation instanceof DefaultValue) {
            DefaultValue value = (DefaultValue) annotation;
            String text = value.value();

            data.setDefault(text);
         } 
         data.addAnnotation(annotation);
      }
      return data;
   }
}
