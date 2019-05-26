package org.simpleframework.module.graph;

import java.util.Set;

import org.simpleframework.module.path.ClassPath;

public class DependencyScanner {
   
   public DependencyGraph create(ClassPath path, Set<Class> internal) {
      return new DependencyGraph(path, internal);
   }
   

}
