package org.simpleframework.module.index;

import java.lang.annotation.Annotation;

import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.ConstructorNode;

import io.github.classgraph.MethodInfo;

class ConstructorIndex extends MethodIndex implements ConstructorNode {

   public ConstructorIndex(ClassPath path, ClassNode parent, MethodInfo info) {
      super(path, parent, info);
   }
   
   @Override
   public <T extends Annotation> T getAnnotation(Class<T> type) {
      String name = type.getName();
      return getAnnotations()
         .stream()
         .filter(annotation -> annotation.getName().equals(name))
         .map(annotation -> annotation.getAnnotation(type))
         .findFirst()
         .orElse(null);
   }
   
   @Override
   public ClassNode getReturnType() {
      return getDeclaringClass(); 
   }
}
