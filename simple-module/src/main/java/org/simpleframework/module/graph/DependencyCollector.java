package org.simpleframework.module.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.ConstructorNode;

public class DependencyCollector {

   private final Map<String, Set<Dependency>> index;
   private final DependencyResolver resolver;
   private final ModuleFilter filter;
   private final ClassPath path;
   
   public DependencyCollector(ModuleFilter filter, ClassPath path) {
      this.resolver = new DependencyResolver(filter, path);
      this.index = new HashMap<>(); 
      this.filter = filter;
      this.path = path;      
   }
   
   public Set<Dependency> collect(String name) {
      return index.computeIfAbsent(name, this::resolve);     
   }
   
   private Set<Dependency> resolve(String name) {      
      ClassNode node = path.getType(name);
      
      if(filter.isComponent(node)) {
         return node.getConstructors()
               .stream()
               .map(this::resolve)
               .flatMap(Set<Dependency>::stream)
               .collect(Collectors.toSet());         
      }
      return Collections.emptySet();            
   }
   
   private Set<Dependency> resolve(ConstructorNode constructor) {
      return constructor.getParameterTypes()
            .stream() 
            .map(resolver::resolve)
            .filter(Objects::nonNull) 
            .collect(Collectors.toSet());
   }
}
