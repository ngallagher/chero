package org.simpleframework.module.graph;

import java.util.Objects;

import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.path.MethodNode;
import org.simpleframework.module.path.ParameterNode;

public class DependencyQualifier {
   
   private static final String INJECT_ATTRIBUTE = "value";
   private static final String PROVIDES_ATTRIBUTE = "value";   
   
   public DependencyQualifier() {    
      super();
   }
   
   public String qualify(Function function) {
      if(function.isAnnotationPresent(Provides.class)) {
         Provides annotation = function.getAnnotation(Provides.class);
         String value = annotation.value();
         
         return value.equals("") ? null : String.valueOf(value);
      }
      return null;
   }
   
   public String qualify(ParameterNode node) {
      String label = Inject.class.getName();
      
      if(node.isAnnotationPresent(label)) {
         String value = node.getAnnotations()
               .stream()
               .filter(annotation -> annotation.getName().equals(label))
               .map(annotation -> annotation.getValues().get(INJECT_ATTRIBUTE))
               .filter(Objects::nonNull)
               .map(Objects::toString)               
               .findFirst()
               .orElse("");
         
         return value.equals("") ? null : String.valueOf(value);
      }
      return null;
   }

   public String qualify(MethodNode node) {
      String label = Provides.class.getName();
      
      if(node.isAnnotationPresent(label)) {
         String value = node.getAnnotations()
               .stream()
               .filter(annotation -> annotation.getName().equals(label))
               .map(annotation -> annotation.getValues().get(PROVIDES_ATTRIBUTE))
               .filter(Objects::nonNull)
               .map(Objects::toString)               
               .findFirst()
               .orElse("");
         
         return value.equals("") ? null : String.valueOf(value);
      }
      return null;
   }
}
