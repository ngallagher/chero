package org.simpleframework.module.argument;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RelativePathScanner implements ResourceScanner {

   private final Set<String> paths;

   public RelativePathScanner(Set<String> paths) {
      this.paths = paths;
   }

   @Override
   public List<URL> scan(Set<String> sources) {
      List<URL> resources = new ArrayList<>();
      Set<String> done = new HashSet<>();

      for (String parent : paths) {
         Path path = Paths.get(parent);

         if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Directory '" + path + "' does not exist");
         }
         for(String source : sources) {
            Path file = path.resolve(source);

            if(Files.isRegularFile(file) && done.add(source)) {
               try {
                  URI target = file.toUri();
                  URL resource = target.toURL();

                  resources.add(resource);
               } catch (Exception e) {
                  throw new IllegalArgumentException("Could not resolve '" + file + "'", e);
               }
            }
         }
      }
      sources.removeAll(done);
      return Collections.unmodifiableList(resources);
   }

   @Override
   public List<URL> scan(Set<String> sources, Set<String> extensions) {
      List<URL> resources = new ArrayList<>();
      Set<String> done = new HashSet<>();

      for (String parent : paths) {
         Path path = Paths.get(parent);

         if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Directory '" + path + "' does not exist");
         }
         scan(sources, extensions, path).forEach((key, value) -> {
            if(done.add(key)) {
               resources.add(value);
            }
         });
      }
      sources.removeAll(done);
      return Collections.unmodifiableList(resources);
   }

   private Map<String, URL> scan(Set<String> files, Set<String> extensions, Path path) {
      Map<String, URL> matches = new HashMap<>();

      for (String name : files) {
         for (String extension : extensions) {
            Path file = resolve(path, name, extension);

            if (file != null) {
               try {
                  URI target = file.toUri();
                  URL resource = target.toURL();

                  if (!matches.containsKey(name)) {
                     matches.put(name, resource);
                  }
               } catch (Exception e) {
                  throw new IllegalArgumentException("Could not resolve '" + file + "'", e);
               }
            }
         }
      }
      return Collections.unmodifiableMap(matches);
   }

   private Path resolve(Path path, String name, String extension) {
      Path absolute = path.resolve(name + extension);

      if (Files.isRegularFile(absolute)) {
         return absolute;
      }
      if (name.endsWith(extension)) {
         Path file = path.resolve(name);

         if (Files.isRegularFile(file)) {
            return file;
         }
      }
      return null;
   }
}
