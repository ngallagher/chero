package org.simpleframework.module.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.index.ModuleFilter;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.MethodNode;
import org.simpleframework.module.path.ParameterNode;

class DependencyResolver {
   
   private final DependencyQualifier qualifier;
   private final Map<String, Dependency> index;
   private final ModuleFilter filter;
   private final ClassPath path;
   
   public DependencyResolver(ModuleFilter filter, ClassPath path) {
      this.qualifier = new DependencyQualifier();
      this.index = new HashMap<>(); 
      this.filter = filter;
      this.path = path;      
   }
   
   public Dependency resolve(ClassNode node) {
      String name = node.getName();
      
      return index.computeIfAbsent(name, key -> {
         ClassNode match = resolveMatch(node);
         return new Dependency(node, match, null);
      });          
   }
   
   public Dependency resolve(ParameterNode node) {
      String name = node.getName();
      String require = qualifier.qualify(node);
      ClassNode type = node.getType();
      
      return index.computeIfAbsent(name, key -> {
         ClassNode match = resolveMatch(node);
         return new Dependency(type, match, require);
      });          
   }
   
   private ClassNode resolveMatch(ParameterNode node) {
      ClassNode type = node.getType();
      
      if(filter.isProvided(type)) {
         Set<ClassNode> nodes = path.getMethods(Provides.class)
            .stream()
            .filter(method -> method.getReturnType().equals(type))
            .filter(method -> {
               String actual = qualifier.qualify(method);
               String require = qualifier.qualify(node);
               
               if(require != null) {
                  return Objects.equals(actual, require);
               }
               return true;
            })
            .map(MethodNode::getReturnType)
            .collect(Collectors.toSet());
         
         if(!filter.isVisible(type)) {
            return resolveInternal(type, nodes);
         }         
      }
      return type;
   }
   
   private ClassNode resolveMatch(ClassNode node) {
      if(!filter.isProvided(node)) {
         Set<ClassNode> nodes = path.getTypes(Component.class);  
         
         if(!filter.isVisible(node)) {
            return resolveInternal(node, nodes);
         }
         if(!filter.isComponent(node)) {
            return resolveComponent(node, nodes);
         }
      }
      return node;
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
