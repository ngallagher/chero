package org.simpleframework.module.index;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.HashCache;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.MethodNode;

class ClassPathIndex implements ClassPath {

   private final Cache<Class, Set<MethodNode>> methods;
   private final Cache<Class, Set<ClassNode>> types;
   private final Cache<String, ClassNode> nodes;
   private final Set<String> packages;
   private final ClassPathLoader loader;
   
   public ClassPathIndex(Cache<String, ClassNode> nodes, Set<String> packages) {
      this.loader = new ClassPathLoader(this);
      this.methods = new HashCache<>();
      this.types = new HashCache<>();
      this.packages = packages;
      this.nodes = nodes;
   }
   
   @Override
   public ClassNode findType(String name) {
      return nodes.fetch(name, loader::loadNode);
   }
   
   @Override
   public Set<MethodNode> findMethods(Class<? extends Annotation> type) {
      Predicate<MethodNode> filter = node -> node.isAnnotationPresent(type);
      
      return methods.fetch(type, ignore -> findTypes()
         .stream()
         .flatMap(node -> node.getMethods().stream())
         .filter(filter)
         .collect(Collectors.toSet()));
   } 
   
   @Override
   public Set<MethodNode> findMethods(Predicate<MethodNode> filter) {
      return findTypes()
         .stream()
         .flatMap(node -> node.getMethods().stream())
         .filter(filter)
         .collect(Collectors.toSet());
   }  
   
   @Override
   public Set<ClassNode> findTypes(Class<? extends Annotation> type) {
      Predicate<ClassNode> filter = node -> node.isAnnotationPresent(type);
      
      return types.fetch(type, ignore -> nodes.values()
         .stream()
         .filter(filter)
         .collect(Collectors.toSet()));
   }      
   
   @Override
   public Set<ClassNode> findTypes(Predicate<ClassNode> filter) {
      return nodes.values()
         .stream()
         .filter(filter)
         .collect(Collectors.toSet());
   }      
   
   @Override
   public Set<ClassNode> findTypes() {
      return nodes.values()
         .stream()
         .collect(Collectors.toSet());
   }
   
   @Override
   public Set<String> findPackages() {
      return packages;
   }
}
