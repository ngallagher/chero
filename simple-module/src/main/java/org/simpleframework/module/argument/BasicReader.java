package org.simpleframework.module.argument;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class BasicReader implements AttributeReader {
   
   private final String extension;

   public BasicReader(String extension) {
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
               InputStreamReader reader = new InputStreamReader(source, "UTF-8");
               LineNumberReader iterator = new LineNumberReader(reader);

               try {
                  while (true) {
                     String line = iterator.readLine();

                     if (line == null) {
                        break;
                     }
                     String token = line.trim();

                     if (!token.startsWith(";") && !token.startsWith("[")) {
                        int index = token.indexOf("=");
                        int length = token.length();

                        if (index != -1 && index < length) {
                           String key = token.substring(0, index).trim();
                           String value = token.substring(index + 1, length).trim();
                           
                           map.put(key, value);
                        }
                     }
                  }
               } catch (Exception e) {
                  iterator.close();
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
