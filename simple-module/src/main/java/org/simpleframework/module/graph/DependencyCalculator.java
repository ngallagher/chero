package org.simpleframework.module.graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

public class DependencyCalculator {
   
   private final DependencyCollector collector;
   private final ModuleFilter filter;
   private final ClassPath path;
   
   public DependencyCalculator(ClassPath path, Set<Class> internal) {
      this.filter = new ModuleFilter(path, internal);
      this.collector = new DependencyCollector(filter, path);
      this.path = path;
   }
   
   public void calculate(Consumer<ClassNode> ready) {
      Set<ClassNode> require = path.getTypes(Component.class);
      
      if(!require.isEmpty()) {
         Set<ClassNode> done = new HashSet<>();
         
         for(ClassNode node: require) {
            calculate(ready, done, node);
         }
      }      
   }
   
   private void calculate(Consumer<ClassNode> ready, Set<ClassNode> done, ClassNode node) {
      if(done.add(node)) {
         String name = node.getName();
         Set<Dependency> children = collector.collect(name);
         Set<ClassNode> missing = children.stream()
               .filter(Dependency::isError)
               .map(Dependency::getNode)
               .filter(filter::isMissing)
               .collect(Collectors.toSet());         
         
         for(ClassNode require : missing) {
            throw new IllegalStateException("Could not resolve " + require + " for " + name);
         }  
         Set<ClassNode> require = children.stream()
               .map(Dependency::getMatch)
               .filter(Objects::nonNull)
               .filter(filter::isComponent)
               .collect(Collectors.toSet());
         
         if(!require.isEmpty()) {
            require.removeAll(done);
            
            for(ClassNode child : require) {
               calculate(ready, done, child);
            }
         }
         ready.accept(node); // all children done         
      }
   }
}
