package org.simpleframework.module.index;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.HashCache;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

class ClassPathIndex implements ClassPath {
      
   private final Cache<Class, Set<ClassNode>> indexes;
   private final Cache<String, ClassNode> types;
   private final Set<String> packages;
   private final ClassPathLoader loader;
   
   public ClassPathIndex(Cache<String, ClassNode> types, Set<String> packages) {
      this.loader = new ClassPathLoader(this);
      this.indexes = new HashCache<>();
      this.packages = packages;
      this.types = types;
   }
   
   @Override
   public ClassNode getType(String name) {
      return types.fetch(name, loader::loadNode);
   }
   
   @Override
   public Set<ClassNode> getTypes(Class<? extends Annotation> type) {
      String name = type.getName();
      Predicate<ClassNode> filter = node -> node.isAnnotationPresent(name);
      
      return indexes.fetch(type, ignore -> types.values()
         .stream()
         .filter(filter)
         .collect(Collectors.toSet())
      );
   }      
   
   @Override
   public Set<ClassNode> getTypes(Predicate<ClassNode> filter) {
      return types.values()
            .stream()
            .filter(filter)
            .collect(Collectors.toSet());
   }      
   
   @Override
   public Set<ClassNode> getTypes() {
      return types.values()
            .stream()
            .collect(Collectors.toSet());
   }
   
   @Override
   public Set<String> getPackages() {
      return packages;
   }
}
