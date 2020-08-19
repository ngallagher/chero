package org.simpleframework.module.argument;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.module.common.ClassPathReader;

public class ClassPathScanner implements ResourceScanner {

   public ClassPathScanner() {
      super();
   }

   @Override
   public List<URL> scan(Iterable<String> files) {
      Map<String, URL> matches = new HashMap<>();
      List<URL> ordered = new ArrayList<>();

      for (String name : files) {
         URL resource = ClassPathReader.findResource(name);

         if (resource != null) {
            matches.put(name, resource);
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

      for (String name : files) {
         for (String extension : extensions) {
            URL resource = resolve(name, extension);

            if (resource != null) {
               if (!matches.containsKey(name)) {
                  matches.put(name, resource);
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
