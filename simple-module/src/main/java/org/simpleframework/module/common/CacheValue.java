package org.simpleframework.module.common;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class CacheValue<T> {
   
   private final AtomicReference<T> reference;
   
   public CacheValue() {
      this.reference = new AtomicReference<T>();
   }
   
   public T get(Supplier<T> supplier) {
      T value = reference.get();
      
      if(value == null) {
         value = supplier.get();
         reference.set(value);
      }
      return value;
   }

}
