package org.simpleframework.module.argument;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AttributeCombiner {

   private final CommandLineParser parser;
   private final ResourceFinder finder;

   public AttributeCombiner(Set<String> paths) {
      this.finder = new ResourceFinder(paths);
      this.parser = new CommandLineParser();
   }

   public Map<String, String> combine(Iterable<String> names, String[] arguments) {
      Map<String, String> overrides = parser.parse(arguments);
      Iterable<String> extensions = AttributeSource.extensions();
      List<URL> resources = finder.find(names, extensions);

      if(resources.isEmpty()) {
         throw new IllegalStateException("No resources found");
      }
      Map<String, String> attributes = new LinkedHashMap<>();

      for(URL resource : resources) {
         AttributeReader reader = AttributeSource.reader(resource);
         Map<String, String> map = reader.read(resource);

         attributes.putAll(map);
      }
      attributes.putAll(overrides);
      return Collections.unmodifiableMap(attributes);
   }
}
