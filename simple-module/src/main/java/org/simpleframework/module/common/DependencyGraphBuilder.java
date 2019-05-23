package org.simpleframework.module.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.simpleframework.module.annotation.Import;
import org.simpleframework.module.annotation.Module;

public class DependencyGraphBuilder {

   private final Set<Class<?>> modules;
   
   public DependencyGraphBuilder(Set<Class<?>> modules) {
      this.modules = modules;
   }
   
   public DependencyGraph create() {
      Set<Class<?>> imports = new HashSet<>();

      for(Class<?> module : modules) {
         collect(module, imports);
      }
      Set<String> packages = reduce(imports);
      String[] patterns = packages.stream()
            .filter(Objects::nonNull)
            .map(module -> module + ".*")
            .toArray(String[]::new);
      
      return new DependencyGraph(packages, patterns);
   }
   
   private Set<String> reduce(Set<Class<?>> imports) {
      Set<String> packages = new HashSet<>();
      List<String> sorted = new ArrayList<>();
      
      for(Class<?> imported : imports) {
         Package module = imported.getPackage();
         String name = module.getName();
         
         sorted.add(name);
      }
      Collections.sort(sorted, (a, b) -> a.length() - b.length());
      
      for(String name : sorted) {
         if(packages.add(name)) {
            String prefix = name;
            
            while(prefix.contains(".")) {
               int index = prefix.lastIndexOf(".");
               
               if(index != -1) {
                  prefix = prefix.substring(0, index);
                  
                  if(packages.contains(prefix)) {
                     packages.remove(name); // a child is already there
                  }
               }
            }
         }
      }
      return packages;
   }
   
   private void collect(Class<?> module, Set<Class<?>> done) {
      if(!module.isAnnotationPresent(Module.class)) {
         throw new IllegalStateException("Import " + module + " is not a module ");
      }
      if(done.add(module)) {
         Import imports = module.getAnnotation(Import.class);
         
         if(imports != null) {
            Class<?>[] dependencies = imports.value();
            
            if(dependencies.length == 0) {
               throw new IllegalStateException("Import on " + module + " does not import any modules");
            }
            for(Class<?> dependency : dependencies) {
               collect(dependency, done);
            }
         }
      }
   }
   
}
