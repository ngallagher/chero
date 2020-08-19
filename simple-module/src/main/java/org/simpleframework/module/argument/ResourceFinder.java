package org.simpleframework.module.argument;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ResourceFinder {

   private final ResourceScanner[] scanners;

   public ResourceFinder(Set<String> paths) {
      this.scanners = new ResourceScanner[] {
           new AbsolutePathScanner(),
           new RelativePathScanner(paths),
           new ClassPathScanner()
      };
   }

   public List<URL> find(String file) {
      for(ResourceScanner scanner : scanners) {
         List<URL> resources = scanner.scan(Arrays.asList(file));

         if(!resources.isEmpty()) {
            return resources;
         }
      }
      return Collections.emptyList();
   }

   public List<URL> find(Iterable<String> files) {
      for(ResourceScanner scanner : scanners) {
         List<URL> resources = scanner.scan(files);

         if(!resources.isEmpty()) {
            return resources;
         }
      }
      return Collections.emptyList();
   }

   public List<URL> find(Iterable<String> files, String extension) {
      for(ResourceScanner scanner : scanners) {
         List<URL> resources = scanner.scan(files, Arrays.asList(extension));

         if(!resources.isEmpty()) {
            return resources;
         }
      }
      return Collections.emptyList();
   }

   public List<URL> find(Iterable<String> files, Iterable<String> extensions) {
      for(ResourceScanner scanner : scanners) {
         List<URL> resources = scanner.scan(files, extensions);

         if(!resources.isEmpty()) {
            return resources;
         }
      }
      return Collections.emptyList();
   }
}
