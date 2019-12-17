package org.simpleframework.module.graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.index.ModuleFilter;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.MethodNode;

class DependencyCalculator {
   
   private final DependencyCollector collector;
   private final ModuleFilter filter;
   private final ClassPath path;
   
   public DependencyCalculator(ModuleFilter filter, ClassPath path) {
      this.collector = new DependencyCollector(filter, path);
      this.filter = filter;
      this.path = path;
   }
   
   public void calculate(Consumer<ClassNode> ready) {
      Set<ClassNode> components = path.getTypes(Component.class);
      Set<ClassNode> modules = path.getTypes(Module.class);
      String label = Provides.class.getName();
      
      if(!components.isEmpty() || !modules.isEmpty()) {
         Set<ClassNode> done = new HashSet<>();

         for(ClassNode module : modules) {
            if(filter.isVisible(module)) {
               calculate(ready, done, module);
            }
         }
         for(ClassNode module : modules) {
            if(filter.isVisible(module)) {
               module.getMethods()
                  .stream()
                  .filter(method -> method.isAnnotationPresent(label))
                  .map(MethodNode::getReturnType)
                  .forEach(node -> calculate(ready, done, node));
            }
         }
         for(ClassNode component : components) {            
            if(filter.isVisible(component)) {
               calculate(ready, done, component);
            }
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
         
         if(!missing.isEmpty()) {
            throw new IllegalStateException("Could not resolve " + missing + " for " + name);
         }  
         children.stream()
            .filter(Objects::nonNull)         
            .forEach(dependency -> {
               ClassNode match = dependency.getMatch();
               
               if(filter.isDependency(match)) {
                  calculate(ready, done, match);                  
               }
            });
         
         ready.accept(node); // all children done         
      }
   }
}
