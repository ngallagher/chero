package org.simpleframework.module.graph.index;

import java.lang.reflect.Array;

public class SystemClassLoader {
   
   private final ClassPath path;
   
   public SystemClassLoader(ClassPath path) {
      this.path = path;
   }

   public ClassNode loadClass(String name) {
      ClassType type = getType(name);
      
      if(type != null) {       
         if(type.isSystem()) {
            Class real = type.getType();
            
            if(real != null) {
               return new SystemNode(real, name);
            }
         }   
         if(type.isArray()) {
            String real = type.getName();
            ClassNode node = path.getType(real);
            int dimensions = type.getDimensions();
            
            if(node != null) {
               return path.getType(real, dimensions);
            }
         }  
      }
      return null;
   }
   
   private static ClassType getType(String name) {
      try {         
         int dimensions = getDimensions(name);
         boolean system = name.startsWith("java.");
         
         return new ClassType(name, system, dimensions);
      } catch(Exception e) {
         return null;
      }
   }
   
   private static int getDimensions(String name) {
      if(name.endsWith("[]")) {
         int width = 0;
        
         while(name.endsWith("[]")) {
            int length = name.length();
            
            name = name.substring(0, length - 2);
            width += 1;
         }
         return width;
      }
      return 0;
   }
   
   private static class ClassType {
      
      private final String name;
      private final int[] dimensions;
      private final boolean system;
      
      public ClassType(String name, boolean system, int dimensions) {
         this.dimensions = new int[dimensions];
         this.system = system;
         this.name = name;
      }
      
      public Class getType() {
         try {
            String name = getName();
            Class type = Class.forName(name);
            
            if(dimensions.length > 0) {
               return Array.newInstance(type, dimensions).getClass();
            }
            return type; 
         } catch(Exception e) {
            return null;
         }
      }
      
      public String getName() {
         if(dimensions.length > 0) {
            int length = name.length();
            int reduce = dimensions.length * 2;
            
            return name.substring(0, length - reduce);
         }
         return name;
      }
      
      public int getDimensions() {
         return dimensions.length;
      }
      
      public boolean isSystem() {
         return system;
      }
      
      public boolean isArray() {
         return dimensions.length > 0;
      }
   }
}
