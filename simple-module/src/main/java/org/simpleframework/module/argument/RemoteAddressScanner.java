package org.simpleframework.module.argument;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RemoteAddressScanner implements ResourceScanner {

   public RemoteAddressScanner() {
      super();
   }

   @Override
   public List<URL> scan(Set<String> sources) {
      List<URL> resources = new ArrayList<>();
      Set<String> done = new HashSet<>();

      for (String source : sources) {
         URL resource = resolve(source, "");

         if (resource != null) {
            if(done.add(source)) {
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

