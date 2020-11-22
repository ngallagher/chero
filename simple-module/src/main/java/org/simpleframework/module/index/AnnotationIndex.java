package org.simpleframework.module.index;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Collectors;

import org.simpleframework.module.common.CacheValue;
import org.simpleframework.module.path.AnnotationNode;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValue;

class AnnotationIndex implements AnnotationNode {

   private final CacheValue<Map<String, Object>> attributes;
   private final AnnotationBuilder builder;
   private final AnnotationInfo info;
   private final ClassPath path;
   
   public AnnotationIndex(ClassPath path, AnnotationInfo info) {
      this.attributes = new CacheValue<Map<String, Object>>();
      this.builder = new AnnotationBuilder(this);
      this.info = info;
      this.path = path;
   }
   
   @Override
   public Annotation getAnnotation() {
      String name = info.getName();
      ClassNode node = path.findType(name);
      Class type = node.getType();
      
      return getAnnotation(type);
   }

   @Override
   public <T extends Annotation> T getAnnotation(Class<T> type) {
      return builder.resolve(type);
   }

   @Override
   public Map<String, Object> getValues() {
      return attributes.get(() -> info.getParameterValues()
         .stream()
         .collect(Collectors.toMap(
               AnnotationParameterValue::getName, 
               AnnotationParameterValue::getValue)));
   }

   @Override
   public String getName() {
      return info.getName();
   }
}
