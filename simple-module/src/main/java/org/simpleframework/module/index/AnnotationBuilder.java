package org.simpleframework.module.index;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.HashCache;
import org.simpleframework.module.extract.StringConverter;
import org.simpleframework.module.path.AnnotationNode;

class AnnotationBuilder {

   private final Cache<Class, Annotation> annotations;
   private final StringConverter converter;
   private final AnnotationNode node;
   
   public AnnotationBuilder(AnnotationNode node) {
      this.converter = new StringConverter();
      this.annotations = new HashCache<>();
      this.node = node;
   }
   
   public <T extends Annotation> T resolve(Class<T> type) {
      return (T)annotations.fetch(type, this::create);
   }
   
   private <T extends Annotation> T create(Class<T> type) {
      Map<String, Object> values = node.getValues();
      ClassLoader loader = type.getClassLoader();
      
      if(!type.isAnnotation()) {
         throw new IllegalStateException("Could not create annotation for " + type);
      }
      Class[] interfaces = new Class[] {type};
      AnnotationHandler handler = new AnnotationHandler(values);
      
      return (T)Proxy.newProxyInstance(loader, interfaces, handler);
   }
   
   private class AnnotationHandler implements InvocationHandler {
      
      private final Cache<String, Object> conversions;
      private final Map<String, Object> attributes;
      
      public AnnotationHandler(Map<String, Object> attributes) {
         this.conversions = new HashCache<>();
         this.attributes = attributes;
      }

      @Override
      public Object invoke(Object proxy, Method method, Object[] values) throws Throwable {
         if(values == null || values.length == 0) {
            String name = method.getName();
            Object attribute = attributes.get(name);
            
            if(attribute == null) {
               return method.invoke(this);
            }
            return conversions.fetch(name, ignore -> {
               Class type = method.getReturnType();
   
               if(type.isArray()) {
                  int length = Array.getLength(attribute);
                  Class entry = type.getComponentType();
                  Object array = Array.newInstance(entry, length);
                  
                  for(int i = 0; i < length; i++) {
                     Object element = Array.get(attribute, i);
                     String text = String.valueOf(element);
                     Object value = converter.convert(entry, text);
                     
                     Array.set(array, i, value);
                  }
                  return array;
               } 
               String text = String.valueOf(attribute);
               return converter.convert(type, text);
            });
         }
         return method.invoke(this, values);
      }
   }
   
}
