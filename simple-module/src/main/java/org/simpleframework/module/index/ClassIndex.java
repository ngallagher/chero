package org.simpleframework.module.index;

import java.net.URL;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.ConstructorNode;
import org.simpleframework.module.path.MethodNode;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;

class ClassIndex implements ClassNode {
   
   private final Function<MethodInfo, ConstructorNode> constructors;
   private final Function<MethodInfo, MethodNode> methods;
   private final ClassPath path;
   private final ClassInfo info;
   
   public ClassIndex(ClassPath path, ClassInfo info) {
      this.constructors = (constructor) -> new ConstructorIndex(path, this, constructor);
      this.methods = (constructor) -> new MethodIndex(path, this, constructor);
      this.path = path;
      this.info = info;
   }
   
   @Override
   public boolean isEnum() {
      return info.isEnum();
   }
   
   @Override
   public boolean isInterface() {
      return info.isInterface();
   }
   
   @Override
   public boolean isAnnotationPresent(String name) {
      return info.hasAnnotation(name);
   }
   
   @Override
   public boolean isSuper(String name) {
      return info.extendsSuperclass(name);
   }     

   @Override
   public List<MethodNode> getMethods() {
      return info.getMethodInfo()
            .stream()
            .map(methods)
            .collect(Collectors.toList());
   }

   @Override
   public List<ConstructorNode> getConstructors() {
      return info.getConstructorInfo()
            .stream()
            .map(constructors)
            .collect(Collectors.toList());
   }
   
   @Override
   public List<ClassNode> getImplementations() {
      return info.getClassesImplementing()
            .stream()
            .map(ClassInfo::getName)
            .map(path::getType)
            .collect(Collectors.toList());
   }

   @Override
   public URL getResource() {
      return info.getClasspathElementURL();
   }
   
   @Override
   public String getName() {
      return info.getName();
   }

   @Override
   public Class getType() {
      return info.loadClass();
   }
   
   @Override
   public String toString() {
      return getName();
   }
}
