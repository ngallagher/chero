package org.simpleframework.module.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.ConstructorNode;

public class DependencyCollector {

   private final Map<String, Set<ClassNode>> index;
   private final DependencyResolver resolver;
   private final DependencyFilter filter;
   private final ClassPath path;
   
   public DependencyCollector(ClassPath path, Set<String> internal) {
      this.filter = new DependencyFilter(path);
      this.resolver = new DependencyResolver(path, internal);
      this.index = new HashMap<>(); 
      this.path = path;      
   }
   
   public Set<ClassNode> collect(String name) {
      return index.computeIfAbsent(name, this::resolve);     
   }
   
   private Set<ClassNode> resolve(String name) {      
      ClassNode node = path.getType(name);
      
      if(filter.test(node)) {         
         String annotation = Component.class.getName();
         
         if(node.isAnnotationPresent(annotation)) {
            return node.getConstructors()
                  .stream()
                  .map(this::resolve)
                  .flatMap(Set<ClassNode>::stream)
                  .collect(Collectors.toSet());
         }
      }
      return Collections.emptySet();            
   }
   
   private Set<ClassNode> resolve(ConstructorNode constructor) {
      return constructor.getParameterTypes()
            .stream() 
            .map(resolver::resolve)
            .filter(Objects::nonNull) 
            .filter(filter)
            .collect(Collectors.toSet());
   }
}
