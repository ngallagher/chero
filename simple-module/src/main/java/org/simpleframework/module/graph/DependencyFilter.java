package org.simpleframework.module.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

public class DependencyFilter implements Predicate<ClassNode> {

   private final Map<String, Boolean> status;
   private final ClassPath path;
   
   public DependencyFilter(ClassPath path) {
      this.status = new HashMap<>();
      this.path = path;      
   }

   @Override
   public boolean test(ClassNode node) {
      String name = node.getName();
      Boolean match = status.get(name);
      
      if(match == null) {
         Set<String> packages = path.getPackages();
         
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
   }
}
