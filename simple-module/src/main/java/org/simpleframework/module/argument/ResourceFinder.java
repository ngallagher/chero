package org.simpleframework.module.argument;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ResourceFinder {

   private final ResourceScanner scanner;

   public ResourceFinder(Set<String> paths) {
      this.scanner = paths.isEmpty() ? new ClassPathScanner() : new FileSystemScanner(paths);
   }

   public List<URL> find(String file) {
      return scanner.scan(Arrays.asList(file));
   }

   public List<URL> find(Iterable<String> files) {
      return scanner.scan(files);
   }

   public List<URL> find(Iterable<String> files, String extension) {
      return scanner.scan(files, Arrays.asList(extension));
   }

   public List<URL> find(Iterable<String> files, Iterable<String> extensions) {
      return scanner.scan(files, extensions);
   }
}
