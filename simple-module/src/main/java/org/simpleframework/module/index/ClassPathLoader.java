package org.simpleframework.module.index;

import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.HashCache;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

class ClassPathLoader {
   
   private final Cache<String, ClassNode> index;
   private final ReferenceIndexLoader loader;
   private final ArrayIndexBuilder builder;
   
   public ClassPathLoader(ClassPath path) {
      this.loader = new ReferenceIndexLoader(path);
      this.builder = new ArrayIndexBuilder(path);
      this.index = new HashCache<>();
   }

   public ClassNode loadNode(String name) {
      return index.fetch(name, key -> {
         ClassNode node = builder.create(name);
         
         if(node == null) {       
            return loader.loadNode(name);
         }
         return null;
      });
   }
   

}
