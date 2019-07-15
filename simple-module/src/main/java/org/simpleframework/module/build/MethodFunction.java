package org.simpleframework.module.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;

public class MethodFunction implements Function {

   private final ComponentManager manager;
   private final ArgumentList arguments;
   private final MethodTarget target;
   private final Method method;

   public MethodFunction(ComponentManager manager, ConstructorScanner scanner, ArgumentList arguments, Method method, Class type) {
      this.target = new MethodTarget(scanner, type);
      this.arguments = arguments;
      this.manager = manager;
      this.method = method;
   }
   
   @Override
   public <T> T getValue(Context context) throws Exception {
      Object destination = target.build(context);
      
      if (destination == null) {
         throw new IllegalStateException("Could not create a component for " + method);
      }
      Object[] values = arguments.create(context);
      Object object = method.invoke(destination, values);

      return (T)object;
   }

   @Override
   public float getScore(Context context) throws Exception {
      return arguments.score(context);
   }
   
   @Override
   public Annotation getAnnotation(Class type) {
      return method.getAnnotation(type);
   }
   
   @Override
   public Annotation[] getAnnotations() {
      return method.getAnnotations();
   }
   
   @Override
   public Annotation[] getDeclaredAnnotations() {
      return method.getDeclaredAnnotations();
   }

   @Override
   public Parameter[] getParameters(){
      return arguments.require();
   }
   
   @Override
   public String getName() {
      return method.getName();
   }
   
   @Override
   public String toString() {
      return String.valueOf(method);
   }
}
