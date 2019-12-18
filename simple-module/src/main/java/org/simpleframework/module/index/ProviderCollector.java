package org.simpleframework.module.index;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.CacheValue;
import org.simpleframework.module.common.HashCache;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.MethodNode;

public class ProviderCollector {
   
   private final Cache<Class, Set<MethodNode>> types;
   private final CacheValue<Set<MethodNode>> all;
   
   public ProviderCollector() {
      this.types = new HashCache<>();
      this.all = new CacheValue<>();
   }
   
   public Set<MethodNode> collect(ModuleFilter filter, ClassPath path) {
      return all.get(() -> path.getTypes(Module.class)
         .stream()
         .filter(filter::isVisible)
         .map(ClassNode::getMethods)
         .flatMap(Collection<MethodNode>::stream)
         .collect(Collectors.toSet()));
   }
   
   public Set<MethodNode> collect(ModuleFilter filter, ClassPath path, ClassNode node) {
      if(node != null) {
         Class type = node.getType();
         
         return types.fetch(type, ignore -> collect(filter, path)
            .stream()
            .filter(method ->
               method.isAnnotationPresent(Provides.class) &&
               method.getReturnType().equals(node))
            .collect(Collectors.toSet()));
      }
      return Collections.emptySet();
   }
}
