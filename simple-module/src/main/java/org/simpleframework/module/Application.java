package org.simpleframework.module;

public class Application {

   public static <T extends ApplicationBuilder> T create(Class<T> driver) {
      try {
         return (T)driver.getDeclaredConstructor().newInstance();
      } catch(Exception e) {
         throw new IllegalStateException("Could not create " + driver, e);
      }
   }
}
