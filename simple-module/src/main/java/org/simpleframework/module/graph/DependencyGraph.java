package org.simpleframework.module.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import io.github.classgraph.ClassGraph;

public class DependencyGraph {

   private final Map<String, Boolean> status;
   private final Set<String> packages;
   private final ClassGraph graph;
   
   public DependencyGraph(Set<String> packages, String[] patterns) {
      this.graph = new ClassGraph().enableAllInfo().whitelistPackages(patterns).whitelistPaths("..");
      this.status = new HashMap<>();
      this.packages = packages;
   }

   public Predicate<String> getPredicate() {
      return name -> {
         Boolean match = status.get(name);
         
         if(match == null) {
            for(String prefix : packages) {
               if(name.startsWith(prefix)) {
                  status.put(name, true);
                  return true;
               }
            }
            status.put(name, false);
            return false;
         }
         return match.booleanValue();
         
      };
   }
   
   public ClassGraph getGraph() {
      return graph;
   }
}
