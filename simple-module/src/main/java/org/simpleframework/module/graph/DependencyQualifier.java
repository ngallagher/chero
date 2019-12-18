package org.simpleframework.module.graph;

import java.util.Objects;

import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.path.MethodNode;
import org.simpleframework.module.path.ParameterNode;

public class DependencyQualifier {
   
   public DependencyQualifier() {    
      super();
   }
   
   public String qualify(Function function) {
      if(function.isAnnotationPresent(Provides.class)) {
         Provides annotation = function.getAnnotation(Provides.class);
         String value = annotation.value();
         
         return Objects.equals("", value) ? null : value;
      }
      return null;
   }
   
   public String qualify(ParameterNode node) {
      if(node.isAnnotationPresent(Provides.class)) {
         Provides annotation = node.getAnnotation(Provides.class);
         String value = annotation.value();
         
         return Objects.equals("", value) ? null : value;
      }
      return null;
   }

   public String qualify(MethodNode node) {
      if(node.isAnnotationPresent(Provides.class)) {
         Provides annotation = node.getAnnotation(Provides.class);
         String value = annotation.value();
         
         return Objects.equals("", value) ? null : value;
      }
      return null;
   }
}
