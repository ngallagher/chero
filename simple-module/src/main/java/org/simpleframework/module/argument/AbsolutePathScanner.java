package org.simpleframework.module.argument;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbsolutePathScanner implements ResourceScanner {

   public AbsolutePathScanner() {
      super();
   }

   @Override
   public List<URL> scan(Set<String> sources) {
      List<URL> resources = new ArrayList<>();
      Set<String> done = new HashSet<>();

      for (String source : sources) {
         Path file = Paths.get(source);

         if (Files.isRegularFile(file) && done.add(source)) {
            try {
               URI target = file.toUri();
               URL resource = target.toURL();

               resources.add(resource);
            } catch (Exception e) {
               throw new IllegalArgumentException("Could not resolve '" + file + "'", e);
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

      for (String source : sources) {
         for (String extension : extensions) {
            Path file = resolve(source, extension);

            if (Files.isRegularFile(file) && done.add(source)) {
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
