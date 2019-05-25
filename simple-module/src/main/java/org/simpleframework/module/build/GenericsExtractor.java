package org.simpleframework.module.build;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericsExtractor {

   public Class[] extract(Type type) throws Exception  {
      if(ParameterizedType.class.isInstance(type)) {
         ParameterizedType real = (ParameterizedType)type;
         Type[] types = real.getActualTypeArguments();
         
         if(types.length > 0) {
            Class[] classes = new Class[types.length];
            
            for(int i = 0; i < types.length; i++) {
               classes[i] = (Class)types[i];
            }
            return classes;
         }
      }
      return new Class[] {};
   }
}
