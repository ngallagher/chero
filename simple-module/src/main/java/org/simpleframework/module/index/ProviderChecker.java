package org.simpleframework.module.index;

import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.MethodNode;

public class ProviderChecker {
   
   public ProviderChecker() {
      super();
   }

   public boolean isProvider(MethodNode method, ClassNode node) {
      if(method.isAnnotationPresent(Provides.class)) {
         return method.getReturnType().equals(node);
      }
      return false;
   }
}
