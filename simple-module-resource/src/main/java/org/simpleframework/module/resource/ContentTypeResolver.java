package org.simpleframework.module.resource;

import java.io.Reader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.ClassPathReader;
import org.simpleframework.module.common.LeastRecentlyUsedCache;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ContentTypeResolver {
   
   private static final String TYPES_FILE = "context/types.json";

   private final Cache<String, String> cache;
   private final Map<String, String> types;
   private final ObjectMapper mapper;
   
   public ContentTypeResolver() {
      this.cache = new LeastRecentlyUsedCache<String, String>(1000);
      this.types = new ConcurrentHashMap<String, String>();
      this.mapper = new ObjectMapper();
   }
   
   private Map<String, String> readTypes() {
      if(types.isEmpty()) {
         try {
            Reader reader = ClassPathReader.findResourceAsReader(TYPES_FILE);
            Map<String, String> map = mapper.readValue(reader, Map.class);
            
            types.putAll(map);
         }catch(Exception e) {
            e.printStackTrace();
         }
      }
      return types;
   }

   public String matchPath(String path) {
      Map<String, String> types = readTypes();
      Set<String> expressions = types.keySet();
      String token = path.toLowerCase();

      for (String expression : expressions) {         
         if (token.equalsIgnoreCase(expression) || token.matches(expression)) {
            String type = types.get(expression);
            
            if(type != null) {             
               return type;
            }
         }
      }
      return "application/octet-stream";
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
}