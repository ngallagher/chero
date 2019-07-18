package org.simpleframework.module.resource;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ContentTypeReader {
   
   private static final String DEFAULT_COMMENT = "#";
   
   private final String comment;

   public ContentTypeReader() {
      this(DEFAULT_COMMENT);
   }
   
   public ContentTypeReader(String comment) {
      this.comment = comment;
   }

   public Map<String, String> read(Reader reader) throws IOException {
      LineNumberReader iterator = new LineNumberReader(reader);
      Map<String, String> types = new HashMap<String, String>();
      
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
