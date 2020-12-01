package org.simpleframework.module.argument;

import static java.util.Collections.EMPTY_SET;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Interpolator;
import org.simpleframework.module.core.EscapeType;
import org.simpleframework.module.core.MapContext;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.core.TokenFilter;
import org.simpleframework.module.core.ContextFilter;

public class ResourceReader {

   private final ResourceFinder finder;

   public ResourceReader() {
      this(EMPTY_SET);
   }

   public ResourceReader(Set<String> paths) {
      this.finder = new ResourceFinder(paths);
   }

   public String read(String file, Context context) {
      List<URL> resources = finder.find(file);

      if (resources.isEmpty()) {
         throw new IllegalArgumentException("Could not find " + file);
      }
      for (URL resource : resources) {
         return read(resource, context);
      }
      return null;
   }

   public String read(String file, Map<String, String> values) {
      List<URL> resources = finder.find(file);
      Set<String> names = values.keySet();

      if (resources.isEmpty()) {
         throw new IllegalArgumentException("Could not find " + file);
      }
      Context context = new MapContext();

      for (String name : names) {
         Model model = context.getModel();
         String value = model.get(name);

         model.set(name, value);
      }
      for (URL resource : resources) {
         return read(resource, context);
      }
      return null;
   }

   private String read(URL resource, Context context) {
      TokenFilter filter = new ContextFilter(context);
      Interpolator interpolator = new Interpolator(filter);
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      byte[] data = new byte[1024];
      int count = 0;

      try {
         InputStream input = resource.openStream();

         while ((count = input.read(data)) != -1) {
            buffer.write(data, 0, count);
         }
         String text = buffer.toString();
         String path = resource.getPath();

         if (!text.isEmpty()) {
            return interpolator.interpolate(text, EscapeType.resolve(path));
         }
      } catch (Exception e) {
         throw new IllegalArgumentException("Could not read " + resource, e);
      }
      return null;
   }
}
