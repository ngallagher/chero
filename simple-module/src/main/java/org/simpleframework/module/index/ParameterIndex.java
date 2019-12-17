package org.simpleframework.module.index;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.simpleframework.module.common.CacheValue;
import org.simpleframework.module.path.AnnotationNode;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.ParameterNode;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.MethodParameterInfo;

class ParameterIndex implements ParameterNode {

   private final Function<AnnotationInfo, AnnotationNode> annotations;
   private final CacheValue<ClassNode> type;
   private final MethodParameterInfo info;
   private final ClassNode parent;
   private final ClassPath path;
   
   public ParameterIndex(ClassPath path, ClassNode parent, MethodParameterInfo info) {
      this.annotations = (annotation) -> new AnnotationIndex(path, annotation); 
      this.type = new CacheValue<>();
      this.parent = parent;
      this.path = path;
      this.info = info;
   }
   
   @Override
   public boolean isAnnotationPresent(String name) {
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
   public ClassNode getType() {      
      return type.get(() ->{
         String name = info.getTypeDescriptor().toString();      
         int index = name.indexOf("<");
   
         if(index != -1) {
            String type = name.substring(0, index);
            return path.getType(type);
         }
         return path.getType(name);
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