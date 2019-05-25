package org.simpleframework.module.extract;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.List;

import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;

public abstract class StringConverterExtractor implements Extractor<Object> {

   private final Class<? extends Annotation> annotation;
   private final StringConverter converter;

   public StringConverterExtractor(Class<? extends Annotation> annotation) {
      this.converter = new StringConverter();
      this.annotation = annotation;
   }

   @Override
   public Object extract(Argument argument, Context context) {
      if (argument != null) {
         List<String> values = resolve(argument, context);
         Class type = argument.getType();

         if (values != null) {
            int size = values.size();
            
            if(type.isArray()) {
               Class entry = type.getComponentType();
               Object list = Array.newInstance(entry, size);
               
               for(int i = 0; i < size; i++) {
                  String value = values.get(i);
                  Object element = converter.convert(entry, value);
                  
                  if(element != null) {
                     Array.set(list, i, element);
                  }
               }
               return list;
            }           
            if(size > 0) {
               String value = values.get(0);
               Object result = converter.convert(type, value);
               
               if(result != null) {
                  return result;
               }
            }
         }
      }
      return null;
   }

   @Override
   public boolean accept(Argument argument) {
      Annotation value = argument.getAnnotation(annotation);
      Class type = argument.getType();

      if (value != null) {
         if(type.isArray()) {
            Class entry = type.getComponentType();
            
            if(entry != null) {
               return converter.accept(entry); 
            }
            return false;
         }
         return converter.accept(type);
      }
      return false;
   }

   protected abstract List<String> resolve(Argument argument, Context context);
}
