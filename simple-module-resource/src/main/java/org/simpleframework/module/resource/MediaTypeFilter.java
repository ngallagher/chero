package org.simpleframework.module.resource;

import org.simpleframework.http.ContentType;

public class MediaTypeFilter {
   
   private final MediaType[] options;
   
   public MediaTypeFilter(MediaType... options) {
      this.options = options;
   }
   
   public boolean accept(ContentType type) throws Exception {
      if(type != null) {
         String value = type.getType();
         String primary = type.getPrimary();
         String secondary = type.getSecondary();
         
         for(MediaType option :  options) {
            if(value.equalsIgnoreCase(option.value)) {
               return true;
            }
            if(primary.equalsIgnoreCase(option.primary)) {
               int index = secondary.indexOf("+");
               
               if(index > 0) {
                  int length = secondary.length();
                  String token = secondary.substring(index + 1, length);
                  
                  if(token.equalsIgnoreCase(option.secondary)) {
                     return true;
                  }
               }
            }
         }
      }
      return false;
   }
}
