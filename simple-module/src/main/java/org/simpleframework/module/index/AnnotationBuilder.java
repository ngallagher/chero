package org.simpleframework.module.index;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.simpleframework.module.path.AnnotationNode;

class AnnotationBuilder {

   public AnnotationBuilder() {
      super();
   }
   
   public <T extends Annotation> T resolve(Class<T> type, AnnotationNode node) {
      Map<String, Object> values = node.getValues();
      ClassLoader loader = type.getClassLoader();
      
      if(type.isAnnotation()) {
         throw new IllegalStateException("Could not resolve annotation for " + type);
      }
      Class[] interfaces = new Class[] {type};
      AnnotationHandler handler = new AnnotationHandler(values);
      
      return (T)Proxy.newProxyInstance(loader, interfaces, handler);
   }
   
   private static class AnnotationHandler implements InvocationHandler {
      
      private final Map<String, Object> attributes;
      
      public AnnotationHandler(Map<String, Object> attributes) {
         this.attributes = attributes;
      }

      @Override
      public Object invoke(Object proxy, Method method, Object[] values) throws Throwable {
         if(values.length == 0) {
            String value = method.getName();
            Object result = attributes.get(value);
            
            if(result != null) {
               return result;
            }
         }
         return method.invoke(this, values);
      }
   }
   
}
