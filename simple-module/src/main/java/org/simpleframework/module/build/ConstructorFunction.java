package org.simpleframework.module.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import org.simpleframework.module.core.Context;

public class ConstructorFunction implements Function {

   private final PropertyInjector injector;
   private final InstanceManager manager;
   private final ArgumentList arguments;
   private final Constructor factory;
   private final Instance argument;

   public ConstructorFunction(InstanceManager manager, ArgumentList arguments, PropertyInjector injector, Constructor factory) {
      this.argument = new Instance(factory);
      this.arguments = arguments;
      this.injector = injector;
      this.factory = factory;
      this.manager = manager;
   }

   @Override
   public <T> T getValue(Context context) throws Exception {
      Object instance = getInstance(context);

      if (injector != null) {
         injector.inject(instance, context);
      }
      return (T)instance;
   }
   
   private <T> T getInstance(Context context) throws Exception {
      Object instance = manager.get(argument, context);
      
      if(instance == null) {
         Object[] values = arguments.create(context);
         Object object = factory.newInstance(values);
   
         if (object != null) {
            manager.set(argument, context, object);
         }
         return (T)object;
      }
      return (T)instance;
   }

   @Override
   public float getScore(Context context) throws Exception {
      if (injector.valid(context)) {
         return arguments.score(context);
      }
      return -1;
   }
   
   @Override
   public Annotation getTypeAnnotation(Class type) {
      return factory.getDeclaringClass().getAnnotation(type);
   }
   
   @Override
   public Annotation getAnnotation(Class type) {
      return factory.getAnnotation(type);
   }
   
   @Override
   public Annotation[] getAnnotations() {
      return factory.getAnnotations();
   }
   
   @Override
   public Annotation[] getDeclaredAnnotations() {
      return factory.getDeclaredAnnotations();
   }

   @Override
   public Parameter[] getParameters(){
      return arguments.require();
   }
   
   @Override
   public Class getReturnType() {
      return factory.getDeclaringClass();
   }
   
   @Override
   public String getName() {
      return factory.getName();
   }
   
   @Override
   public String toString() {
      return String.valueOf(factory);
   }
}
