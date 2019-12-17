package org.simpleframework.module.graph;

import org.simpleframework.module.path.ClassNode;

class Dependency {

   private final ClassNode match;
   private final ClassNode node;
   private final String qualifier;
   private final String name;
   
   public Dependency(ClassNode node, ClassNode match, String qualifier) {
      this.name = node.getName();
      this.qualifier = qualifier;
      this.match = match;
      this.node = node;
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
   
   public String getQualifier() {
      return qualifier;
   }   
   
   public String getName() {
      return name;
   }
   
   @Override
   public String toString() {
      return name;
   }
}
