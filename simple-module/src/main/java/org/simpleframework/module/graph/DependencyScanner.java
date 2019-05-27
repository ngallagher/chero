package org.simpleframework.module.graph;

import org.simpleframework.module.index.ModuleFilter;
import org.simpleframework.module.path.ClassPath;

public class DependencyScanner {
   
   public DependencyGraph create(ModuleFilter filter, ClassPath path) {
      return new DependencyGraph(filter, path);
   }
   

}
