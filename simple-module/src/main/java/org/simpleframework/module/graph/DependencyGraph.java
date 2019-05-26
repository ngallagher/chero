package org.simpleframework.module.graph;

import java.util.Set;
import java.util.function.Function;

import org.simpleframework.module.path.ClassPath;

public class DependencyGraph {
      
   private final DependencyCalculator calculator;
   
   public DependencyGraph(ClassPath path, Set<Class> internal) {
      this.calculator = new DependencyCalculator(path, internal);
   }
   
   public void traverse(Function<Class, Object> builder) {
      calculator.calculate(node -> {
         Class type = node.getType();
         Object instance = builder.apply(type);
         
         if(instance == null) {
            throw new IllegalStateException("Could not resolve " + type);
         }
      });
   }
}
