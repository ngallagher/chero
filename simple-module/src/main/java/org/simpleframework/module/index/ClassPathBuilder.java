package org.simpleframework.module.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.CopyOnWriteCache;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

import io.github.classgraph.ClassInfo;

public class ClassPathBuilder {

   private final Cache<String, ClassNode> empty;
   private final ModuleScopeResolver builder;

   public ClassPathBuilder(Set<Class> modules) {
      this(modules, Collections.EMPTY_SET);
   }
   
   public ClassPathBuilder(Set<Class> modules, Set<String> paths) {
      this.builder = new ModuleScopeResolver(modules, paths);
      this.empty = new EmptyCache<String, ClassNode>();
   }

   public ClassPath create() {
      ModuleScope scope = builder.resolve();
      Set<String> packages = scope.getPackages();
      
      if(!packages.isEmpty()) {
         Map<String, ClassNode> nodes = new HashMap<>();
         Cache<String, ClassNode> cache = new CopyOnWriteCache<>();
         ClassPath path = new ClassPathIndex(cache, packages);
         Function<ClassInfo, ClassIndex> converter = info -> new ClassIndex(path, info);         
         Iterator<ClassIndex> iterator = scope.getGraph()
               .scan()
               .getAllClasses()
               .stream()
               .map(converter)            
               .iterator();
   
         if (iterator.hasNext()) {         
            while (iterator.hasNext()) {
               ClassNode next = iterator.next();
               String name = next.getName();
               
               nodes.put(name, next);
            }
            cache.cache(nodes);
            return path;
         }
         return path;
      }
      return new ClassPathIndex(empty, packages);
   }   
   
   private static class EmptyCache<K, V> extends CopyOnWriteCache<K, V> {

      @Override
      public V cache(K key, V value) {
         return null;
      }
   }
}
