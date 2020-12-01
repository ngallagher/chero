package org.simpleframework.module.core;

public enum EscapeType {
   NONE(".*") {
      @Override
      public void escape(StringBuilder builder, CharSequence data, int off, int length) {
         builder.append(data, off, length);
      }
   },
   JSON(".json") {
      @Override
      public void escape(StringBuilder builder, CharSequence text, int off, int length) {
         for (int i = 0; i < length; i++) {
            char next = text.charAt(off + i);

            if (next == '\\') {
               builder.append('\\');
            } else if (next == '\"') {
               builder.append('\\');
            }
            builder.append(next);
         }
      }
   };

   public final String extension;

   private EscapeType(String extension) {
      this.extension = extension;
   }

   public abstract void escape(StringBuilder builder, CharSequence text, int off, int length);

   public static EscapeType resolve(String path) {
      EscapeType[] types = EscapeType.values();

      for (EscapeType type : types) {
         if (path.endsWith(type.extension)) {
            return type;
         }
      }
      return EscapeType.NONE;
   }
}

