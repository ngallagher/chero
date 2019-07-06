package org.simpleframework.module.index;

import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.CopyOnWriteCache;
import org.simpleframework.module.path.ArrayNode;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

class ArrayIndexBuilder {
   
   private final Cache<String, ArrayNode> index;
   private final ClassPath path;
   
   public ArrayIndexBuilder(ClassPath path) {
      this.index = new CopyOnWriteCache<>();
      this.path = path;
   }
   
   public ArrayNode create(String name) {     
      return index.fetch(name, this::resolve);
   }

   private ArrayNode resolve(String name) {
      Array array = parse(name);
      
      if(array != null) {    
         return resolve(array);
      }
      return null;
   }
   
   private ArrayNode resolve(Array array) {
      int dimensions = array.getDimensions();
      String element = array.getName();   
      String original = array.getType();
      ClassNode node = path.getType(element);
      
      if(node != null) {
         return new ArrayIndex(node, original, dimensions);
      }
      return null;
   }
   
   private Array parse(String name) {
      if(name.endsWith("[]")) {
         String original = name;
         int width = 0;
        
         while(name.endsWith("[]")) {
            int length = name.length();
            
            name = name.substring(0, length - 2);
            width += 1;
         }
         return new Array(original, name, width);
      }
      return null;
   }
   
   private static class Array {
      
      private final String original;
      private final String name;
      private final int dimensions;
      
      public Array(String original, String name, int dimensions) {
         this.dimensions = dimensions;
         this.original = original;
         this.name = name;
      }
      
      public String getType() {
         return original;
      }
      
      public String getName() {
         return name;
      }
      
      public int getDimensions() {
         return dimensions;
      }
   }
}
