package org.simpleframework.module.argument;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PathCombiner {

   private final Set<String> paths;
   private final Set<String> files;

   public PathCombiner(Set<String> files) {
      this(files, Collections.EMPTY_SET);
   }
   
   public PathCombiner(Set<String> files, Set<String> paths) {
      this.paths = paths;
      this.files = files;
   }
   
   public Set<String> combine() {
      Set<String> matches = new HashSet<>();
      
      for(String name : files) {
         Path path = Paths.get(".", name);
         String normal = path.toString();
         
         matches.add(normal);
      }
      for(String parent : paths) {
         for(String name : files) {
            Path path = Paths.get(parent, name);
            String normal = path.toString();
            
            matches.add(normal);
         }
      }
      return Collections.unmodifiableSet(matches);
   }
}
