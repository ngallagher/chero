package org.simpleframework.module.index;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.simpleframework.module.common.CacheValue;
import org.simpleframework.module.path.AnnotationNode;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.MethodNode;
import org.simpleframework.module.path.ParameterNode;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodParameterInfo;

class MethodIndex implements MethodNode {
   
   private final Function<MethodParameterInfo, ParameterNode> parameters;
   private final Function<AnnotationInfo, AnnotationNode> annotations;
   private final CacheValue<ClassNode> returns;
   private final ClassNode parent;
   private final ClassPath path;
   private final MethodInfo info;
   
   public MethodIndex(ClassPath path, ClassNode parent, MethodInfo info) {
      this.parameters = (parameter) -> new ParameterIndex(path, parent, parameter);
      this.annotations = (annotation) -> new AnnotationIndex(path, annotation);
      this.returns = new CacheValue<>();
      this.parent = parent;
      this.path = path;
      this.info = info;
   }
   
   @Override
   public boolean isAnnotationPresent(Class<? extends Annotation> type) {
      String name = type.getName();
      return info.hasAnnotation(name);
   }
   
   @Override
   public List<AnnotationNode> getAnnotations() {
      return info.getAnnotationInfo()
         .stream()
         .map(annotations)
         .collect(Collectors.toList());
   }   

   @Override
   public List<ParameterNode> getParameters() {
      MethodParameterInfo[] params = info.getParameterInfo();
      return Arrays.asList(params)
         .stream() 
         .filter(Objects::nonNull)
         .map(parameters)
         .collect(Collectors.toList());      
   }
   
   @Override
   public ClassNode getReturnType() {
      return returns.get(() -> {
         String name = info.getTypeSignatureOrTypeDescriptor().getResultType().toString();
         int index = name.indexOf("<");
   
         if(index != -1) {
            String type = name.substring(0, index);
            return path.findType(type);
         }
         return path.findType(name);
      });
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