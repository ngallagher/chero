package org.simpleframework.module.graph;

import java.util.function.Function;

import org.simpleframework.module.index.ModuleFilter;
import org.simpleframework.module.path.ClassPath;

public class DependencyGraph {

   private final DependencyCalculator calculator;

   public DependencyGraph(ModuleFilter filter, ClassPath path) {
      this.calculator = new DependencyCalculator(filter, path);
   }

   public void traverse(Function<Class, Object> builder) {
      calculator.calculate(node -> {
         Class type = node.getType();
         Object instance = builder.apply(type);

         if (instance == null) {
            throw new IllegalStateException("Could not resolve " + type);
         }
      });
   }
}
