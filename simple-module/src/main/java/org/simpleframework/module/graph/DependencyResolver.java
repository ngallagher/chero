package org.simpleframework.module.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

public class DependencyResolver {
   
   private final Map<String, ClassNode> index;
   private final DependencyFilter filter;
   private final ClassPath path;
   private final Set<String> internal;
   
   public DependencyResolver(ClassPath path, Set<String> internal) {  
      this.filter = new DependencyFilter(path);
      this.index = new HashMap<>(); 
      this.internal = internal;
      this.path = path;      
   }
   
   public ClassNode resolve(ClassNode node) {
      String name = node.getName();   
      
      return index.computeIfAbsent(name, key -> {
         Set<ClassNode> nodes = path.getTypes(Component.class);  
         return resolve(node, nodes);
      });
   }
   
   private ClassNode resolve(ClassNode node, Set<ClassNode> nodes) {
      if (filter.test(node)) {         
         if(!nodes.contains(node)) {
            return resolveInternal(node, nodes);
         }
         return node;
      }
      return resolveExternal(node, nodes);
   }

   private ClassNode resolveExternal(ClassNode node, Set<ClassNode> nodes) {
      if(node.isInterface()) {
         return node.getImplementations()
               .stream()
               .filter(entry -> {
                  String type = entry.getName();
                  return internal.contains(type);
               }) 
               .findFirst()
               .orElse(null);
      }
      return null;
   }
   
   private ClassNode resolveInternal(ClassNode node, Set<ClassNode> nodes) {
      String name = node.getName();
      
      if(node.isInterface()) {
         return node.getImplementations()
               .stream()
               .filter(entry -> {
                  String type = entry.getName();
                  return !internal.contains(type);
               })               
               .findFirst()
               .orElse(null);
      }
      return nodes.stream()
            .filter(next -> next.isSuper(name))
            .findFirst()
            .orElse(null);
   }
}
