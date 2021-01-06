package org.simpleframework.module.argument;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourceFinder {

   private final ResourceScanner[] scanners;

   public ResourceFinder(Set<String> paths) {
      this.scanners = new ResourceScanner[]{
           new RelativePathScanner(paths),
           new AbsolutePathScanner(),
           new ClassPathScanner(),
           new RemoteAddressScanner()
      };
   }

   public List<URL> find(String source) {
      List<Throwable> errors = new ArrayList<>();
      List<URL> found = new ArrayList<>();
      Set<String> sources = new HashSet<>();

      sources.add(source);

      for (ResourceScanner scanner : scanners) {
         try {
            List<URL> resources = scanner.scan(sources);

            if (!resources.isEmpty()) {
               found.addAll(resources);
            }
         } catch (Throwable cause) {
            errors.add(cause);
         }
      }
      if (!sources.isEmpty()) {
         for (Throwable cause : errors) {
            throw new RuntimeException("Could not find " + sources, cause);
         }
      }
      return found;
   }

   public List<URL> find(Set<String> sources) {
      List<Throwable> errors = new ArrayList<>();
      List<URL> found = new ArrayList<>();

      for (ResourceScanner scanner : scanners) {
         try {
            List<URL> resources = scanner.scan(sources);

            if (!resources.isEmpty()) {
               found.addAll(resources);
            }
         } catch (Throwable cause) {
            errors.add(cause);
         }
      }
      if (!sources.isEmpty()) {
         for (Throwable cause : errors) {
            throw new RuntimeException("Could not find " + sources, cause);
         }
      }
      return found;
   }

   public List<URL> find(Set<String> sources, String extension) {
      List<Throwable> errors = new ArrayList<>();
      List<URL> found = new ArrayList<>();
      Set<String> extensions = new HashSet<>();

      sources.add(extension);

      for (ResourceScanner scanner : scanners) {
         try {
            List<URL> resources = scanner.scan(sources, extensions);

            if (!resources.isEmpty()) {
               found.addAll(resources);
            }
         } catch (Throwable cause) {
            errors.add(cause);
         }
      }
      if (!sources.isEmpty()) {
         for (Throwable cause : errors) {
            throw new RuntimeException("Could not find " + sources, cause);
         }
      }
      return found;
   }

   public List<URL> find(Set<String> sources, Set<String> extensions) {
      List<Throwable> errors = new ArrayList<>();
      List<URL> found = new ArrayList<>();

      for (ResourceScanner scanner : scanners) {
         try {
            List<URL> resources = scanner.scan(sources, extensions);

            if (!resources.isEmpty()) {
               found.addAll(resources);
            }
         } catch (Throwable cause) {
            errors.add(cause);
         }
      }
      if (!sources.isEmpty()) {
         for (Throwable cause : errors) {
            throw new RuntimeException("Could not find " + sources, cause);
         }
      }
      return found;
   }
}
