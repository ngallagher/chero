package org.simpleframework.module.argument;

import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesReader implements AttributeReader {

   @Override
   public Map<String, String> read(Reader reader) {
      Map<String, String> map = new LinkedHashMap<>();
      Properties properties = new Properties();

      try {
         properties.load(reader);
         
         if(!properties.isEmpty()) {
            Set<String> keys = properties.stringPropertyNames();
            
            for(String key : keys) {
               String name = key.trim();
               String value = properties.getProperty(name, "");
               String token = value.trim();
               
               map.put(name, token);
            }
         }
      } catch (Exception e) {
         throw new IllegalStateException("Could not parse configuration file", e);
      }
      return map;
   }
}
