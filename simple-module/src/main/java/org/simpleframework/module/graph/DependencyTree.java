package org.simpleframework.module.graph;

import java.util.Queue;

public class DependencyTree {

   private final DependencyPath path;
   private final Queue<Class> types;
   
   public DependencyTree(DependencyPath path, Queue<Class> types) {
      this.types = types;
      this.path = path;
   }
   
   public DependencyPath getPath() {
      return path;
   }
   
   public Queue<Class> getOrder() {
      return types;
   }
}
