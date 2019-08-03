package org.simpleframework.resource.build;

public class PathFormatter {
   
   public PathFormatter() {
      super();
   }

   public String formatPath(String methodPath) {
      return new HyphenSplitter(methodPath).process();
   }
   
   private class HyphenSplitter extends PathSplitter {
      
      private HyphenSplitter(String source) {
         super(source);
      }
      
      @Override
      protected void parse(char[] text, int off, int len) {
         text[off] = toLower(text[off]);
      }

      @Override
      protected void commit(char[] text, int off, int len) {
         builder.append(text, off, len);
         
         if(off + len < count) {
            char next = text[off + len];
            
            if(!isSpecial(next)) {
               builder.append('-');
            }
         }
      }
   }
}
