package org.simpleframework.module.core;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapModel implements Model {

   private final Map<Object, Object> context;

   public MapModel() {
      this.context = new LinkedHashMap<>();
   }

   @Override
   public boolean isEmpty() {
      return context.isEmpty();
   }

   @Override
   public Iterator<Object> iterator() {
      return context.keySet().iterator();
   }

   @Override
   public <T> T remove(Object key) {
      return (T)context.remove(key);
   }

   @Override
   public <T> T get(Object key) {
      return (T)context.get(key);
   }

   @Override
   public boolean contains(Object key) {
      return context.containsKey(key);
   }

   @Override
   public void set(Object key, Object object) {
      context.put(key, object);      
   }
}
