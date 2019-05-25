package org.simpleframework.module.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;

public class Instance implements Argument {
   
   private final Constructor<?> factory;
   
   public Instance(Constructor<?> factory) {
      this.factory = factory;
   }

   @Override
   public <T extends Annotation> T getAnnotation(Class<T> type) {
      return factory.getDeclaringClass().getAnnotation(type);
   }

   @Override
   public boolean isConstructor() {
      return false;
   }

   @Override
   public boolean isRequired() {
      return false;
   }

   @Override
   public boolean isCollection() {
      return false;
   }

   @Override
   public Member getSource() {
      return factory;
   }

   @Override
   public String getDefault() {
      return null;
   }

   @Override
   public Class getEntry() {
      return null;
   }

   @Override
   public Class getType() {
      return factory.getDeclaringClass();
   }

}
