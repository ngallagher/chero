package org.simpleframework.module.index;

import java.util.Set;

import io.github.classgraph.ClassGraph;

class ModuleScope {

   private final PackageList packages;
   private final ClassGraph graph;
   
   public ModuleScope(Set<Class> modules) {
      this.graph = new ClassGraph()
            .enableAllInfo()
            .whitelistPaths("..");
      this.packages = new PackageList(graph, modules);
   }

   public Set<String> getPackages() {
      return packages.list();
   }
   
   public ClassGraph getGraph() {
      return graph;
   }
}
