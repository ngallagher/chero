package org.simpleframework.module.index;

import java.util.Set;

import io.github.classgraph.ClassGraph;

public class ModuleScope {

   private final Set<String> packages;
   private final ClassGraph graph;
   
   public ModuleScope(Set<String> packages, String[] patterns, String[] paths) {
      this.graph = new ClassGraph()
            .enableAllInfo()
            .whitelistPackages(patterns)
            .whitelistPaths(paths);
      this.packages = packages;
   }

   public Set<String> getPackages() {
      return packages;
   }
   
   public ClassGraph getGraph() {
      return graph;
   }
}