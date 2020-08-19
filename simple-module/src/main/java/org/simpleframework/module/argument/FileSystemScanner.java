package org.simpleframework.module.argument;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileSystemScanner implements ResourceScanner {

   private final Set<String> paths;

   public FileSystemScanner(Set<String> paths) {
      this.paths = paths;
   }

   @Override
   public List<URL> scan(Iterable<String> files) {
      Map<String, URL> matches = new HashMap<>();
      List<URL> ordered = new ArrayList<>();

      for (String parent : paths) {
         Path path = Paths.get(parent);

         if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Directory '" + path + "' does not exist");
         }
         for(String name : files) {
            Path file = path.resolve(name);

            if(Files.isRegularFile(file)) {
               try {
                  URI target = file.toUri();
                  URL resource = target.toURL();

                  matches.put(name, resource);
               } catch (Exception e) {
                  throw new IllegalArgumentException("Could not resolve '" + file + "'", e);
               }
            }
         }
      }
      for (String name : files) {
         URL path = matches.get(name);

         if (path != null) {
            ordered.add(path);
         }
      }
      return Collections.unmodifiableList(ordered);
   }

   @Override
   public List<URL> scan(Iterable<String> files, Iterable<String> extensions) {
      Map<String, URL> matches = new HashMap<>();
      List<URL> ordered = new ArrayList<>();

      for (String parent : paths) {
         Path path = Paths.get(parent);

         if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Directory '" + path + "' does not exist");
         }
         scan(files, extensions, path).forEach(matches::put);
      }
      for (String name : files) {
         URL path = matches.get(name);

         if (path != null) {
            ordered.add(path);
         }
      }
      return Collections.unmodifiableList(ordered);
   }

   private Map<String, URL> scan(Iterable<String> files, Iterable<String> extensions, Path path) {
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
