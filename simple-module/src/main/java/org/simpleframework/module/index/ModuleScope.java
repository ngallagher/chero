package org.simpleframework.module.index;

import java.util.Set;

import org.simpleframework.module.common.CacheValue;

import io.github.classgraph.ClassGraph;

class ModuleScope {

   private final CacheValue<Set<String>> prefixes;
   private final PackageList packages;
   private final ClassGraph graph;
   
   public ModuleScope(Set<Class> modules) {
      this.graph = new ClassGraph()
            .enableAllInfo()
            .whitelistPaths("..");
      this.packages = new PackageList(graph, modules);
      this.prefixes = new CacheValue<>();
   }

   public Set<String> getPackages() {
      return prefixes.get(() -> packages.list());
   }
   
   public ClassGraph getGraph() {
      return graph;
   }
}
