package org.simpleframework.module.path;

import java.lang.annotation.Annotation;
import java.util.List;

public interface ParameterNode {
   
   default <T extends Annotation> T getAnnotation(Class<T> type) {
      String name = type.getName();
      return getAnnotations()
         .stream()
         .filter(annotation -> annotation.getName().equals(name))
         .map(annotation -> annotation.getAnnotation(type))
         .findFirst()
         .orElse(null);
      
   }
   
   boolean isAnnotationPresent(Class<? extends Annotation> type);
   List<AnnotationNode> getAnnotations();
   ClassNode getDeclaringClass();
   ClassNode getType();
   String getName();
}
