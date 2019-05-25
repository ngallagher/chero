package org.simpleframework.module.build;

public enum Modifier {
   CONSTRUCTOR(0x01),
   METHOD(0x02),
   REQUIRED(0x04),
   INJECTABLE(0x08);
   
   public final int mask;
   
   private Modifier(int mask) {
      this.mask = mask;
   }
   
   public static boolean isConstructor(int modifier){
      return modifier >= 0 && (CONSTRUCTOR.mask & modifier) != 0;
   }
   
   public static boolean isMethod(int modifier){
      return modifier >= 0 && (METHOD.mask & modifier) != 0;
   }
   
   public static boolean isRequired(int modifier){
      return modifier >= 0 && (REQUIRED.mask & modifier) != 0;
   }
   
   public static boolean isInjectable(int modifier){
      return modifier >= 0 && (INJECTABLE.mask & modifier) != 0;
   }
}
