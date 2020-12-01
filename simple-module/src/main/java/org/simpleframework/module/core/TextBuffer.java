package org.simpleframework.module.core;

public interface TextBuffer {

   default void append(char[] text) {
      append(text, 0, text.length);
   }

   default void append(CharSequence text) {
      append(text, 0, text.length());
   }

   void append(char[] text, int off, int length);
   void append(CharSequence text, int off, int length);
}
