package org.simpleframework.module.path;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public interface ClassNode {
   
   default boolean isEnum() {
      return false;
   }
   
   default boolean isArray() {
      return false;
   }
   
   default boolean isInterface() {
      return false;
   }
   
   default boolean isAnnotationPresent(Class<? extends Annotation> type) {
      return false;
   }
   
   default boolean isSuper(String name) {
      return false;
   }
   
   default ClassNode getSuper() {
      return null;
   }
   
   default List<ClassNode> getImplementations() {
      return Collections.emptyList();
   }
   
   default List<FieldNode> getFields() {
      return Collections.emptyList();
   }
   
   default List<MethodNode> getMethods() {
      return Collections.emptyList();
   }
   
   default List<ConstructorNode> getConstructors() {
      return Collections.emptyList();
   }
   
   default List<AnnotationNode> getAnnotations() {
      return Collections.emptyList();
   }

   default <T extends Annotation> T getAnnotation(Class<T> type) {
      String name = type.getName();
      return getAnnotations()
         .stream()
         .filter(annotation -> annotation.getName().equals(name))
         .map(annotation -> annotation.getAnnotation(type))
         .findFirst()
         .orElse(null);
      
   }
   
   default String getQualifier() {
      return getName();
   }
   
   URL getResource();
   String getName();
   Class getType(); 
}
