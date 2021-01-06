package org.simpleframework.module.argument;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.simpleframework.module.common.ClassPathReader;

public class ClassPathScanner implements ResourceScanner {

   public ClassPathScanner() {
      super();
   }

   @Override
   public List<URL> scan(Set<String> sources) {
      List<URL> resources = new ArrayList<>();
      Set<String> done = new HashSet<>();

      for (String source : sources) {
         URL resource = ClassPathReader.findResource(source);

         if (resource != null) {
            if (done.add(source)) {
               resources.add(resource);
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
            URL resource = resolve(source, extension);

            if (resource != null) {
               if (done.add(source)) {
                  resources.add(resource);
               }
            }
         }
      }
      sources.removeAll(done);
      return Collections.unmodifiableList(resources);
   }

   private URL resolve(String name, String extension) {
      URL resource = ClassPathReader.findResource(name + extension);

      if (resource == null) {
         if (name.endsWith(extension)) {
            return ClassPathReader.findResource(name);
         }
      }
      return resource;
   }
}
