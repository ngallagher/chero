package org.simpleframework.module.core;

import java.util.Map;

public class MapFilter implements TokenFilter {

   private final Map<String, ?> context;

   public MapFilter(Map<String, ?> context) {
      this.context = context;
   }

   @Override
   public void replace(TextBuffer buffer, char[] data, int off, int len) {
      String name = new String(data, off, len);

      if (!name.isEmpty()) {
         String value = token(name);

         if (value != null) {
            buffer.append(value);
         } else {
            buffer.append("${");
            buffer.append(name);
            buffer.append("}");
         }
      }
   }

   protected String token(String name) {
      Object value = context.get(name);

      if (value != null) {
         return String.valueOf(value);
      }
      String property = System.getProperty(name);

      if(property == null) {
         return System.getenv(name);
      }
      return property;
   }
}