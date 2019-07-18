package org.simpleframework.module.resource;

import java.io.Reader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.ClassPathReader;
import org.simpleframework.module.common.LeastRecentlyUsedCache;

public class MediaTypeResolver {
   
   private static final String DEFAULT_TYPE = "application/octet-stream";
   private static final String DEFAULT_FILE = "mime.types";

   private final Cache<String, String> cache;
   private final Map<String, String> types;
   private final MediaTypeReader reader;
   private final String file;
   
   public MediaTypeResolver() {
      this(DEFAULT_FILE);
   }
   
   public MediaTypeResolver(String file) {
      this(file, 5000);
   }
   
   public MediaTypeResolver(String file, int capacity) {
      this.cache = new LeastRecentlyUsedCache<String, String>(capacity);
      this.types = new ConcurrentHashMap<String, String>();
      this.reader = new MediaTypeReader();
      this.file = file;
   }  

   public String resolveType(String path) {
      String result = cache.fetch(path);
      
      if(result == null) {
         String type = matchPath(path);
         
         if(type != null) {
            cache.cache(path, type);
            return type;
         }
      }
      return result;
   }
   
   private String matchPath(String path) {
      Map<String, String> types = readTypes();
      
      if(!types.isEmpty()) {
         Set<String> extensions = types.keySet();
         String token = path.toLowerCase();
   
         for (String extension : extensions) {         
            if (token.endsWith(extension)) {
               String type = types.get(extension);
               
               if(type != null) {             
                  return type;
               }
            }
         }
      }
      return DEFAULT_TYPE;
   } 
   
   private Map<String, String> readTypes() {
      if(types.isEmpty()) {
         try {
            Reader source = ClassPathReader.findResourceAsReader(file);
            Map<String, String> map = reader.read(source);
            
            types.putAll(map);
         }catch(Exception e) {
            e.printStackTrace();
         }
      }
      return types;
   }
}