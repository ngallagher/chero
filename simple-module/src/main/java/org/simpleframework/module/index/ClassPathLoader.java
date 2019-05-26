package org.simpleframework.module.index;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

class ClassPathLoader {
   
   private final Map<String, ClassNode> index;
   private final ReferenceIndexLoader loader;
   private final ArrayIndexBuilder builder;
   
   public ClassPathLoader(ClassPath path) {
      this.loader = new ReferenceIndexLoader(path);
      this.builder = new ArrayIndexBuilder(path);
      this.index = new ConcurrentHashMap<>();
   }

   public ClassNode loadNode(String name) {
      return index.computeIfAbsent(name, key -> {
         ClassNode node = builder.create(name);
         
         if(node == null) {       
            return loader.loadNode(name);
         }
         return null;
      });
   }
   

}
