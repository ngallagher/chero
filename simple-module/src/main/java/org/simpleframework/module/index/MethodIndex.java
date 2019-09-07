package org.simpleframework.module.index;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.MethodNode;

import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodParameterInfo;
import io.github.classgraph.TypeSignature;

class MethodIndex implements MethodNode {
   
   private final ClassNode parent;
   private final ClassPath path;
   private final MethodInfo info;
   
   public MethodIndex(ClassPath path, ClassNode parent, MethodInfo info) {
      this.parent = parent;
      this.path = path;
      this.info = info;
   }
   
   @Override
   public boolean isAnnotationPresent(String name) {
      return info.hasAnnotation(name);
   }
   
   @Override
   public ClassNode getReturnType() {
      String name = info.getTypeSignatureOrTypeDescriptor().getResultType().toString();
      int index = name.indexOf("<");

      if(index != -1) {
         String type = name.substring(0, index);
         return path.getType(type);
      }
      return path.getType(name);
   }

   @Override
   public List<ClassNode> getParameterTypes() {
      MethodParameterInfo[] params = info.getParameterInfo();         
      return Arrays.asList(params)
            .stream() 
            .filter(Objects::nonNull)
            .map(MethodParameterInfo::getTypeDescriptor)
            .map(TypeSignature::toString)
            .map(path::getType)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
   }

   @Override
   public ClassNode getDeclaringClass() {
      return parent;
   }
   
   @Override
   public String getName() {
      return info.getName();
   }
   
   @Override
   public String toString() {
      return info.toString();
   }
}