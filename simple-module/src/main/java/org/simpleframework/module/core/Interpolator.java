package org.simpleframework.module.core;

public class Interpolator {

   private final Context context;

   public Interpolator(Context context) {
      this.context = context;
   }

   public Object interpolate(Object value) {
      if (value instanceof String) {
         return interpolate((String) value);
      }
      return value;
   }

   public String interpolate(String text) {
      return interpolate(text, EscapeType.NONE);
   }

   public String interpolate(String text, EscapeType type) {
      if (text != null && text.indexOf('$') != -1) {
         StringBuilder builder = new StringBuilder();

         if (!text.isEmpty()) {
            char[] data = text.toCharArray();

            interpolate(builder, type, data);
         }
         return builder.toString();
      }
      return text;
   }

   private void interpolate(StringBuilder builder, EscapeType type, char[] data) {
      for (int i = 0; i < data.length; i++) {
         if (data[i] == '$') {
            if (i + 1 < data.length && data[i + 1] == '{') {
               int start = i + 2;
               int mark = i;
               int size = 0;

               for (i = start; i < data.length; i++) {
                  char next = data[i];

                  if (next == '}') {
                     size = i - start;
                     break;
                  }
               }
               if (size > 0) {
                  replace(builder, type, data, start, size);
               } else {
                  builder.append(data, mark, i - mark);
               }
            } else {
               builder.append(data[i]);
            }
         } else {
            builder.append(data[i]);
         }
      }
   }

   private void replace(StringBuilder builder, EscapeType type, char[] data, int off, int len) {
      String name = new String(data, off, len);

      if (!name.isEmpty()) {
         String value = token(name);

         if (value != null) {
            builder.append(type.escape(value));
         } else {
            builder.append("${");
            builder.append(name);
            builder.append("}");
         }
      }
   }

   private String token(String name) {
      Model model = context.getModel();
      
      if(model != null) {
         Object value = model.get(name);
         
         if (value != null) {    
            return String.valueOf(value);
         }
         return System.getProperty(name);
      }
      return null;
   }
}
