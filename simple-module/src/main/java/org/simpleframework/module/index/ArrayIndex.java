package org.simpleframework.module.index;

import java.lang.reflect.Array;
import java.net.URL;

import org.simpleframework.module.path.ArrayNode;
import org.simpleframework.module.path.ClassNode;

class ArrayIndex implements ArrayNode {
   
   private final ClassNode element;
   private final String name;
   private final int[] dimensions;

   public ArrayIndex(ClassNode element, String name, int width) {
      this.dimensions = new int[width];
      this.element = element;
      this.name = name;
   }
   
   @Override
   public boolean isArray() {
      return true;
   }
   
   @Override
   public int getDimensions() {         
      return dimensions.length;
   }
   
   @Override
   public Class getType() {
      Class type = element.getType();         
      Object empty = Array.newInstance(type, dimensions);
      
      return empty.getClass();
   }
   
   @Override
   public String getName() {
      return name;
   }
   
   @Override
   public URL getResource() {
      return element.getResource();
   }    
}
