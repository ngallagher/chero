package org.simpleframework.module.index;

import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.ConstructorNode;

import io.github.classgraph.MethodInfo;

class ConstructorIndex extends MethodIndex implements ConstructorNode {

   public ConstructorIndex(ClassPath path, ClassNode parent, MethodInfo info) {
      super(path, parent, info);
   }
   
   @Override
   public ClassNode getReturnType() {
      return getDeclaringClass(); 
   }
}
