package org.simpleframework.module.argument;

import java.io.LineNumberReader;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

public class BasicReader implements AttributeReader {

   @Override
   public Map<String, String> read(Reader reader) {
      Map<String, String> map = new LinkedHashMap<>();
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
         throw new IllegalStateException("Could not parse configuration file", e);
      }
      return map;
   }
}
