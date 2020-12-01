package org.simpleframework.module.core;

public class ContextFilter implements TokenFilter {

   private final Context context;

   public ContextFilter(Context context) {
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