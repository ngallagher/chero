package org.simpleframework.module.argument;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class AttributeCombiner {

   private final CommandLineParser parser;
   private final Set<String> paths;
   
   public AttributeCombiner(Set<String> paths) {
      this.parser = new CommandLineParser();
      this.paths = paths;
   }
   
   public Map<String, String> combine(String[] arguments) {
      Map<String, String> map = parser.parse(arguments);
      AttributeSource[] sources = AttributeSource.values();
      
      for(AttributeSource source : sources) {
         AttributeReader reader = source.reader();
         
         if(reader.exists(paths)) {
            Map<String, String> base = reader.read(paths);
            base.putAll(map);
            return Collections.unmodifiableMap(base);
         }
      }
      return Collections.unmodifiableMap(map);
   }
}
