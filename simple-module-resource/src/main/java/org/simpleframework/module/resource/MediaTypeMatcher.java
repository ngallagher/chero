package org.simpleframework.module.resource;

import static org.simpleframework.http.Protocol.ACCEPT;

import java.util.List;
import java.util.function.BiFunction;

import org.simpleframework.http.Request;
import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.LeastRecentlyUsedCache;

public class MediaTypeMatcher {

   private final BiFunction<String, String, Boolean> starts;
   private final BiFunction<String, String, Boolean> ends;
   private final Cache<String, String> cache;
   private final List<String> types;
   private final String header;
   
   public MediaTypeMatcher(List<String> types, String header) {
      this.starts = (pattern, type) -> pattern.equals("*/") || type.startsWith(pattern);
      this.ends = (pattern, type) -> pattern.equals("/*") || type.endsWith(pattern);
      this.cache = new LeastRecentlyUsedCache<>();
      this.header = header;
      this.types = types;
   }
   
   public boolean accept(Request request) {
      String accept = request.getValue(header);
      
      if(accept != null) {
         if(!types.isEmpty()) {
            return match(request) != null;
         }
      }
      return true;
   }
   
   public String match(Request request) {
      String accept = request.getValue(header);
      
      if(accept != null) {
         if(!types.isEmpty()) {
            return cache.fetch(accept, type -> resolve(request));
         }
      }
      return null;
   }
   
   private String resolve(Request request) {
      List<String> accepts = request.getValues(header);
      
      for(String accept : accepts) {
         String pattern = accept.toLowerCase();
         int length = pattern.length();
         int index = pattern.indexOf("/");
         
         if(index != -1) {
            String primary = pattern.substring(0, index + 1); // "application/"
            String secondary = pattern.substring(index, length); // "/json"
            
            for(String type : types) {
               if(starts.apply(primary, type) && ends.apply(secondary, type)) {
                  return type;
               }
            }  
         } else {
            if(pattern.equals("*")) {
               for(String type : types) {
                  return type;
               }
            }
         }
      }
      return null;
   }
}
