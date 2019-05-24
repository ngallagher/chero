package org.simpleframework.module.argument;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

public class YamlReader implements AttributeReader {
   
   private final String extension;

   public YamlReader(String extension) {
      this.extension = extension;
   }

   @Override
   public Map<String, String> read(String... paths) {
      Map<String, String> map = new LinkedHashMap<>();

      for(String path : paths) {
         File file = new File(".", path + extension);

         if(file.exists() && map.isEmpty()) {
            try {
               InputStream source = new FileInputStream(file);
               Yaml yaml = new Yaml();

               try {
                  Map<String, Object> properties = yaml.load(source);
                  
                  if(!properties.isEmpty()) {
                     Set<String> keys = properties.keySet();
                     
                     for(String key : keys) {
                        String name = key.trim();
                        Object value = properties.get(name);
                        String token = String.valueOf(value).trim();
                        
                        map.put(name, token);
                     }
                  }
               } catch (Exception e) {
                  source.close();
               }
            } catch(Exception e){}
         }
      }
      return map;
   }
   
   @Override
   public boolean exists(String... paths) {
      for(String path : paths) {
         File file = new File(".", path + extension);
   
         if(file.exists() && file.isFile()) {
            return true;
         }
      }
      return false;
   }
}
