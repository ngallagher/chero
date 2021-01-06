package org.simpleframework.module.argument;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

      for (ResourceScanner scanner : scanners) {
         try {
            List<URL> resources = scanner.scan(Arrays.asList(source));

            if (!resources.isEmpty()) {
               found.addAll(resources);
            }
         } catch (Throwable cause) {
            errors.add(cause);
         }
      }
      if (found.isEmpty()) {
         for (Throwable cause : errors) {
            throw new RuntimeException("Could not find resource", cause);
         }
      }
      return found;
   }

   public List<URL> find(Iterable<String> sources) {
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
      if (found.isEmpty()) {
         for (Throwable cause : errors) {
            throw new RuntimeException("Could not find resource", cause);
         }
      }
      return found;
   }

   public List<URL> find(Iterable<String> sources, String extension) {
      List<Throwable> errors = new ArrayList<>();
      List<URL> found = new ArrayList<>();

      for (ResourceScanner scanner : scanners) {
         try {
            List<URL> resources = scanner.scan(sources, Arrays.asList(extension));

            if (!resources.isEmpty()) {
               found.addAll(resources);
            }
         } catch (Throwable cause) {
            errors.add(cause);
         }
      }
      if (found.isEmpty()) {
         for (Throwable cause : errors) {
            throw new RuntimeException("Could not find resource", cause);
         }
      }
      return found;
   }

   public List<URL> find(Iterable<String> sources, Iterable<String> extensions) {
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
      if (found.isEmpty()) {
         for (Throwable cause : errors) {
            throw new RuntimeException("Could not find resource", cause);
         }
      }
      return found;
   }
}
