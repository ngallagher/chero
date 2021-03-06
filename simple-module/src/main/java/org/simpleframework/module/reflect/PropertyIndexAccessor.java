package org.simpleframework.module.reflect;

import static org.simpleframework.module.reflect.PropertyAccessor.getMethod;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

public class PropertyIndexAccessor implements Accessor {
   
   private final Method method;
   private final Integer index;
   private final Class entry;
   private final String name;
   
   public PropertyIndexAccessor(String name, Class type, Class entry, Integer index) {
      this.method = getMethod(name, type);
      this.entry = entry;
      this.index = index;
      this.name = name;
   }
   
   @Override
   public String getName() {
      return name + "[" + index + "]";
   }

   @Override
   public Class getType() {
      return entry;
   }    

   @Override
   public <T> T getValue(Object source) {
      try {
         Object value = method.invoke(source);        
         Class type = method.getReturnType();
         
         if(value != null) {
            if(type.isArray()) {
               return (T)Array.get(value, index);
            }
            return (T)((List)value).get(index);
         }
      } catch (Exception e) {
         throw new IllegalStateException("Could not acquire value at index " + index, e);
      }
      return null;
   }  
}