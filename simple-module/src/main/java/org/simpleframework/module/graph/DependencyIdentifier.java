package org.simpleframework.module.graph;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.build.Function;

public class DependencyIdentifier {

   public String identify(Function function) {
      if(function.isConstructor()) {
         Component component = function.getTypeAnnotation(Component.class);
         
         if(component != null) {
            return component.value().equals("") ? null : component.value();
         }
      } else {
         Provides provider = function.getAnnotation(Provides.class);
         
         if(provider != null) {
            return provider.value().equals("") ? null : provider.value();
         }
      }
      return null;
   }
}
