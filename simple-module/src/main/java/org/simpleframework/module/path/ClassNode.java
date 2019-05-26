package org.simpleframework.module.path;

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
   
   default boolean isAnnotationPresent(String name) {
      return false;
   }
   
   default boolean isSuper(String name) {
      return false;
   }
   
   default List<ClassNode> getImplementations() {
      return Collections.emptyList();
   }
   
   default List<MethodNode> getMethods() {
      return Collections.emptyList();
   }
   
   default List<ConstructorNode> getConstructors() {
      return Collections.emptyList();
   }
   
   URL getResource();
   String getName();  
   Class getType(); 
}
