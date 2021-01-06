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

public class AbsolutePathScanner implements ResourceScanner {

   public AbsolutePathScanner() {
      super();
   }

   @Override
   public List<URL> scan(Iterable<String> source) {
      Map<String, URL> matches = new HashMap<>();
      List<URL> ordered = new ArrayList<>();

      for (String name : source) {
         Path file = Paths.get(name);

         if (Files.isRegularFile(file)) {
            try {
               URI target = file.toUri();
               URL resource = target.toURL();

               matches.put(name, resource);
            } catch (Exception e) {
               throw new IllegalArgumentException("Could not resolve '" + file + "'", e);
            }
         }
      }
      for (String name : source) {
         URL path = matches.get(name);

         if (path != null) {
            ordered.add(path);
         }
      }
      return Collections.unmodifiableList(ordered);
   }

   @Override
   public List<URL> scan(Iterable<String> sources, Iterable<String> extensions) {
      Map<String, URL> matches = new HashMap<>();
      List<URL> ordered = new ArrayList<>();

      for (String name : sources) {
         for (String extension : extensions) {
            Path file = resolve(name, extension);

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
      for (String name : sources) {
         URL path = matches.get(name);

         if (path != null) {
            ordered.add(path);
         }
      }
      return Collections.unmodifiableList(ordered);
   }

   private Path resolve(String name, String extension) {
      Path absolute = Paths.get(name + extension);

      if (Files.isRegularFile(absolute)) {
         return absolute;
      }
      if (name.endsWith(extension)) {
         Path file = Paths.get(name);

         if (Files.isRegularFile(file)) {
            return file;
         }
      }
      return null;
   }
}
