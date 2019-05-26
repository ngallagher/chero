package org.simpleframework.module.graph.index;

import java.net.URL;

public class SystemNode implements ClassNode {
   
   private final Class<?> type;
   private final String name;
   
   public SystemNode(Class<?> type, String name) {
      this.name = name;
      this.type = type;
   }

   @Override
   public boolean isEnum() {
      return type.isEnum();
   }
   
   @Override
   public boolean isArray() {
      return type.isArray();
   }
   
   @Override
   public boolean isInterface() {
      return type.isInterface();
   }
   
   @Override
   public boolean isAnnotationPresent(String name) {
      return false;
   }
   
   @Override
   public boolean isSuper(String name) {
      return false;
   }

   @Override
   public URL getResource() {
      return null;
   }
   
   @Override
   public String getName() {
      return name;
   }
   
   @Override
   public Class<?> getType() {
      return type;
   }
   
   @Override
   public String toString() {
      return name;
   }
}
