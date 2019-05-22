package org.simpleframework.module.common;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import io.github.classgraph.ClassGraph;

public class DependencyGraph {

   private final Set<String> packages;
   private final Set<String> reject;
   private final Set<String> accept;
   private final ClassGraph graph;
   
   public DependencyGraph(Set<String> packages, String[] patterns) {
      this.reject = new HashSet<>();
      this.accept = new HashSet<>();
      this.graph = new ClassGraph()
         .enableAllInfo()
         .whitelistPackages(patterns)
         .whitelistPaths("..");
      this.packages = packages;
   }

   public Predicate<String> getPredicate() {
      return name -> {
         if(accept.contains(name)) {
            return true;
         }
         if(reject.contains(name)) {
            return false;
         }
         for(String prefix : packages) {
            if(name.startsWith(prefix)) {
               accept.add(name);
               return true;
            }
         }
         reject.add(name);
         return false;
      };
   }
   
   public ClassGraph getGraph() {
      return graph;
   }
}
