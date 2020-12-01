package org.simpleframework.module.core;

public class EscapeBuffer implements TextBuffer {

   private final StringBuilder builder;
   private final TextWrapper wrapper;
   private final EscapeType type;
   
   public EscapeBuffer(StringBuilder builder, EscapeType type) {
      this.wrapper = new TextWrapper();
      this.builder = builder;
      this.type = type;
   }
   
   @Override
   public void append(char[] text, int off, int length) {
      type.escape(builder, wrapper.with(text, off, length), 0, length);
   }

   @Override
   public void append(CharSequence text, int off, int length) {
      type.escape(builder, text, off, length);
   }

   private static class TextWrapper implements CharSequence {

      private char[] text;
      private int offset;
      private int length;

      public TextWrapper with(char[] text, int offset, int length) {
         this.length = length;
         this.offset = offset;
         this.text = text;
         return this;
      }

      @Override
      public int length() {
         return length;
      }

      @Override
      public char charAt(int index) {
         return text[offset + index];
      }

      @Override
      public TextWrapper subSequence(int start, int end) {
         return with(text, offset + start, end - start);
      }
   }
}
