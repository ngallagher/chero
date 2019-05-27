package org.simpleframework.module.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.index.ModuleFilter;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

class DependencyResolver {
   
   private final Map<String, Dependency> index;
   private final ModuleFilter filter;
   private final ClassPath path;
   
   public DependencyResolver(ModuleFilter filter, ClassPath path) {  
      this.index = new HashMap<>(); 
      this.filter = filter;
      this.path = path;      
   }
   
   public Dependency resolve(ClassNode node) {
      String name = node.getName();
      
      return index.computeIfAbsent(name, key -> {
         Set<ClassNode> nodes = path.getTypes(Component.class);  
         ClassNode match = resolve(node, nodes);
         
         return new Dependency(node, match);
      });          
   }
   
   private ClassNode resolve(ClassNode node, Set<ClassNode> nodes) {
      if (filter.isVisible(node)) {         
         if(!filter.isComponent(node)) {
            return resolveComponent(node, nodes);
         }
         return node;
      }
      return resolveInternal(node, nodes);
   }

   private ClassNode resolveInternal(ClassNode node, Set<ClassNode> nodes) {   
      if(node.isInterface()) {
         return node.getImplementations()
               .stream()
               .filter(filter::isInternal) 
               .findFirst()
               .orElse(null);
      }
      return null;
   }
   
   private ClassNode resolveComponent(ClassNode node, Set<ClassNode> nodes) {
      String name = node.getName();
    
      if(node.isInterface()) {
         return node.getImplementations()
               .stream()
               .filter(filter::isComponent)               
               .findFirst()
               .orElse(null);
      }
      return nodes.stream()
            .filter(next -> next.isSuper(name))
            .filter(filter::isComponent)                
            .findFirst()
            .orElse(null);
   }
}
