package org.simpleframework.resource;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MediaTypeReader {
   
   private static final String DEFAULT_COMMENT = "#";
   
   private final String comment;

   public MediaTypeReader() {
      this(DEFAULT_COMMENT);
   }
   
   public MediaTypeReader(String comment) {
      this.comment = comment;
   }

   public Map<String, String> read(Reader reader) throws IOException {
      LineNumberReader iterator = new LineNumberReader(reader);
      Map<String, String> types = new LinkedHashMap<String, String>();
      
      try {
         String line = iterator.readLine();
         
         while(line != null) {
            String token = line.trim();
            
            if(!token.isEmpty() && !line.startsWith(comment)) {
               String[] parts = token.toLowerCase().split("\\s+");
               String type = parts[0];
               
               for(int i = 1; i < parts.length; i++) {
                  types.put("." + parts[i], type);
               }
            }
            line = iterator.readLine();
         }
      } finally {
         iterator.close();
      }
      return Collections.unmodifiableMap(types);
   }
}
