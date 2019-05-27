package org.simpleframework.module.argument;

import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

public class YamlReader implements AttributeReader {
   
   private final YamlCollector converter;
   
   public YamlReader() {
      this.converter = new YamlCollector();
   }

   @Override
   public Map<String, String> read(Reader reader) {
      Map<String, String> map = new LinkedHashMap<>();
      Yaml yaml = new Yaml();

      try {
         Map<String, Object> properties = yaml.load(reader);
         
         if(!properties.isEmpty()) {
            Set<String> keys = properties.keySet();
            
            for(String key : keys) {
               String name = key.trim();
               Object value = properties.get(name);
               
               converter.collect(map, name, value);
            }
         }
      } catch (Exception e) {
         throw new IllegalStateException("Could not parse file", e);
      }
      return map;
   }
    
}
