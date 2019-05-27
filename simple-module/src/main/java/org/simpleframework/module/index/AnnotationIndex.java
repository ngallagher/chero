package org.simpleframework.module.index;

import java.util.Map;
import java.util.stream.Collectors;

import org.simpleframework.module.path.AnnotationNode;
import org.simpleframework.module.path.ClassPath;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValue;

class AnnotationIndex implements AnnotationNode {
   
   private final AnnotationInfo info;
   private final ClassPath path;
   
   public AnnotationIndex(ClassPath path, AnnotationInfo info) {
      this.info = info;
      this.path = path;
   }

   @Override
   public Map<String, Object> getValues() {
      return info.getParameterValues()
            .stream()
            .collect(Collectors.toMap(AnnotationParameterValue::getName, AnnotationParameterValue::getValue));
   }

   @Override
   public String getName() {
      return info.getName();
   }

}
