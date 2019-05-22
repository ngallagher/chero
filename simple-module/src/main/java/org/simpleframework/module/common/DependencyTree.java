package org.simpleframework.module.common;

import java.lang.annotation.Annotation;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.github.classgraph.ClassInfo;

public class DependencyTree {

   private final DependencyPath path;
   private final Queue<Class> types;
   
   public DependencyTree(DependencyPath path, Queue<Class> types) {
      this.types = types;
      this.path = path;
   }
   
   public Set<Class> getTypes(Class<? extends Annotation> type) {
      Predicate<String> filter = path.getPredicate();
      String name = type.getName();
      
      return path.getObjects()
            .values()
            .stream()
            .filter(info -> {
               String qualifier = info.getName();
            
               if(filter.test(qualifier)) {
                  return info.hasAnnotation(name);
               }
               return false;
            })
            .map(ClassInfo::loadClass)
            .collect(Collectors.toSet());
   }
   
   public Queue<Class> getOrder() {
      return types;
   }
}
