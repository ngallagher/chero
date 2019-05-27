package org.simpleframework.module.graph;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.simpleframework.module.path.ClassNode;

class Dependency {

   private final ClassNode match;
   private final ClassNode node;
   private final String name;

   public Dependency(ClassNode node) {
      this(node, node);
   }
   public Dependency(ClassNode node, ClassNode match) {
      this.name = node.getName();
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
   
   @Override
   public String toString() {
      return name;
   }
}
