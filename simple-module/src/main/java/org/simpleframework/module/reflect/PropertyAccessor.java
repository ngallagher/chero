package org.simpleframework.module.reflect;

import java.lang.reflect.Method;

public class PropertyAccessor implements Accessor {

   private final Method method;
   private final String name;

   public PropertyAccessor(String name, Class type) {
      this.method = getMethod(name, type);
      this.name = name;
   }
   
   @Override
   public String getName() {
      return name;
   }

   @Override
   public Class getType() {
      return method.getReturnType();
   }

   @Override
   public <T> T getValue(Object source) {
      try {
         if(source != null) {
            return (T) method.invoke(source);
         }
      } catch (Exception e) {
         throw new IllegalStateException("Could not acquire value", e);
      }
      return null;
   }
   
   public static String getProperty(Method method) {
      Class[] types = method.getParameterTypes();
      
      if(types.length == 0) {
         return getProperty(method);
      }
      return null;
   }
   
   public static String getProperty(String method) {
      Prefix[] prefixes = Prefix.values();
      int length = method.length() + 1;
      
      for(Prefix prefix : prefixes) {
         int required = prefix.prefix.length() + 1;
         
         if(required <= length) {
            if(method.startsWith(prefix.prefix)) {
               return prefix.getProperty(method);
            }
         }
      }
      return null;
   }

   public static Method getMethod(String name, Class type) {
      Class base = type;
      
      while(type != null) {
         Method method = getMethod(name, type, Prefix.GET);

         if (method == null) {
            method = getMethod(name, type, Prefix.IS);
         }
         if (method != null) {
            return method;
         }
         type = type.getSuperclass();
      }
      throw new IllegalArgumentException("No property named '" + name + "' in " + base);
   }

   public static Method getMethod(String name, Class type, Prefix prefix) {
      Method[] methods = type.getDeclaredMethods();
      String property = prefix.getMethod(name);
      Method match = null;

      for (Method method : methods) {
         Class[] parameterTypes = method.getParameterTypes();
         String methodName = method.getName();

         if (parameterTypes.length == 0) {
            if (methodName.equals(property)) {
               method.setAccessible(true);
               return method;
            }
         }
         if (parameterTypes.length == 1) {
            if(methodName.equals(property)) {
               method.setAccessible(true);               
               match = method;
            }
         }
      }
      return match;
   }

   private static enum Prefix {
      IS("is", true), 
      GET("get", true),
      SET("set", false);

      public final String prefix;
      public final boolean read;

      private Prefix(String prefix, boolean read) {
         this.prefix = prefix;
         this.read = read;
      }
      
      public String getProperty(String name) {
         int length = prefix.length();
         char initial = name.charAt(length);
         String end = name.substring(length + 1);
         char lowerCase = Character.toLowerCase(initial);

         return String.format("%s%s", lowerCase, end);
      }

      public String getMethod(String name) {
         char initial = name.charAt(0);
         char upperCase = Character.toUpperCase(initial);
         String end = name.substring(1);

         return String.format("%s%s%s", prefix, upperCase, end);
      }
   }
}