package org.simpleframework.module.graph;

import org.simpleframework.module.path.ClassNode;

public class Dependency {

   private final ClassNode match;
   private final ClassNode node;
   private final String name;
   
   public Dependency(ClassNode node, ClassNode match, String name) {
      this.match = match;
      this.node = node;
      this.name = name;
   }
   
   public boolean isError() {
      return match == null;
   }
   
   public ClassNode getNode() {
      return node;
   }
   
   public ClassNode getMatch() {
      return match;
   }   
   
   @Override
   public String toString() {
      return name;
   }
}
