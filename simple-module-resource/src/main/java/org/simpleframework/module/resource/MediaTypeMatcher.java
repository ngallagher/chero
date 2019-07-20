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
   
   public MediaTypeMatcher(List<String> types) {
      this.starts = (pattern, type) -> pattern.startsWith("*/") || pattern.startsWith(type);
      this.ends = (pattern, type) -> pattern.endsWith("/*") || pattern.endsWith(type);
      this.cache = new LeastRecentlyUsedCache<>();
      this.types = types;
   }
   
   public String accept(Request request) {
      String accept = request.getValue(ACCEPT);
      
      if(accept != null) {
         return cache.fetch(accept, type -> resolve(request));
      }
      return null;
   }
   
   private String resolve(Request request) {
      List<String> accepts = request.getValues(ACCEPT);
      
      for(String accept : accepts) {
         String pattern = accept.toLowerCase();
         
         for(String type : types) {
            if(starts.apply(pattern, type) && ends.apply(pattern, type)) {
               return type;
            }
         }  
      }
      return null;
   }
}
