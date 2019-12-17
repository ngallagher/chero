package org.simpleframework.module.graph;

import java.util.Objects;

import org.simpleframework.module.annotation.Inject;
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
      String label = Inject.class.getName();
      
      if(node.isAnnotationPresent(label)) {
         String value = node.getAnnotations()
            .stream()
            .filter(annotation -> annotation.getName().equals(label))
            .map(annotation -> annotation.getAnnotation(Inject.class))
            .map(Inject::value)          
            .findFirst()
            .orElse(null);
         
         return Objects.equals("", value) ? null : value;
      }
      return null;
   }

   public String qualify(MethodNode node) {
      String label = Provides.class.getName();
      
      if(node.isAnnotationPresent(label)) {
         String value = node.getAnnotations()
            .stream()
            .filter(annotation -> annotation.getName().equals(label))
            .map(annotation -> annotation.getAnnotation(Provides.class))
            .map(Provides::value)               
            .findFirst()
            .orElse(null);
         
         return Objects.equals("", value) ? null : value;
      }
      return null;
   }
}
