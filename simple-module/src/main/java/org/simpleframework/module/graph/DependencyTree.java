package org.simpleframework.module.graph;

import java.util.Queue;

import org.simpleframework.module.graph.index.ClassPath;

public class DependencyTree {

   private final ClassPath path;
   private final Queue<Class> types;
   
   public DependencyTree(ClassPath path, Queue<Class> types) {
      this.types = types;
      this.path = path;
   }
   
   public ClassPath getPath() {
      return path;
   }
   
   public Queue<Class> getOrder() {
      return types;
   }
}
