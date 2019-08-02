package org.simpleframework.module.index;

import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.MethodNode;

public class ProviderChecker {
   
   private final String annotation;
   
   public ProviderChecker() {
      this.annotation = Provides.class.getName();
   }

   public boolean isProvider(MethodNode method, ClassNode node) {
      if(method.isAnnotationPresent(annotation)) {
         return method.getReturnType().equals(node);
      }
      return false;
   }
}
