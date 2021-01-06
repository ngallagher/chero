package org.simpleframework.module.argument;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteAddressScanner implements ResourceScanner {

   public RemoteAddressScanner() {
      super();
   }

   @Override
   public List<URL> scan(Iterable<String> sources) {
      Map<String, URL> matches = new HashMap<>();
      List<URL> ordered = new ArrayList<>();

      for (String source : sources) {
         URL resource = resolve(source, "");

         if (resource != null) {
            matches.put(source, resource);
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

   @Override
   public List<URL> scan(Iterable<String> sources, Iterable<String> extensions) {
      Map<String, URL> matches = new HashMap<>();
      List<URL> ordered = new ArrayList<>();

      for (String source : sources) {
         for (String extension : extensions) {
            URL resource = resolve(source, extension);

            if (resource != null) {
               if (!matches.containsKey(source)) {
                  matches.put(source, resource);
               }
            }
         }
      }
      for (String source : sources) {
         URL path = matches.get(source);

         if (path != null) {
            ordered.add(path);
         }
      }
      return Collections.unmodifiableList(ordered);
   }

   private URL resolve(String source, String extension) {
      try {
         if (source.startsWith("http://")) {
            return new URL(source + extension);
         }
         if (source.startsWith("https://")) {
            return new URL(source + extension);
         }
      } catch (Throwable cause) {
         return null;
      }
      return null;
   }
}

