package org.simpleframework.module.argument;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class AttributeCombiner {

   private final CommandLineParser parser;
   private final PathCombiner combiner;
   
   public AttributeCombiner(Set<String> files) {
      this(files, Collections.EMPTY_SET);
   }
   
   public AttributeCombiner(Set<String> files, Set<String> paths) {
      this.combiner = new PathCombiner(files, paths);
      this.parser = new CommandLineParser();
   }
   
   public Map<String, String> combine(String[] arguments) {
      Map<String, String> map = parser.parse(arguments);
      AttributeSource[] sources = AttributeSource.values();
      Set<String> paths = combiner.combine();
      
      for(AttributeSource source : sources) {
         AttributeReader reader = source.reader();
         
         if(reader.exists(paths, source.extension)) {
            Map<String, String> base = reader.read(paths, source.extension);
            base.putAll(map);
            return Collections.unmodifiableMap(base);
         }
      }
      return Collections.unmodifiableMap(map);
   }
}
