package org.simpleframework.module.argument;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesReader implements AttributeReader {
   
   private final String extension;

   public PropertiesReader(String extension) {
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
               Properties properties = new Properties();

               try {
                  properties.load(source);
                  
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
                  source.close();
               }
            } catch(Exception e){}
         }
      }
      return map;
   }
   
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
