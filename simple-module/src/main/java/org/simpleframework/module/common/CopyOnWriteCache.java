package org.simpleframework.module.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class CopyOnWriteCache<K, V> implements Cache<K, V> {

   private volatile MapUpdater updater;
   private volatile Map<K, V> cache;
   
   public CopyOnWriteCache() {
      this(16);
   }
   
   public CopyOnWriteCache(int size) {
      this(16, 0.5f);
   }
   
   public CopyOnWriteCache(int size, float density) {
      this.updater = new MapUpdater(size, density);
      this.cache = new HashMap<K, V>();
   }

   @Override
   public Set<K> keySet() {
      return cache.keySet();
   }
   
   @Override
   public Collection<V> values() {
      return cache.values();
   }

   @Override
   public V take(K key) {
      return updater.take(key);
   }

   @Override
   public V fetch(K key) {
      return cache.get(key);
   } 
   
   @Override
   public V fetch(K key, Function<K, V> builder) {
      V value = cache.get(key);
      
      if(value == null) {
         value = builder.apply(key);
         
         if(value != null) {
            cache(key, value);
         }
      }
      return value;
   } 

   @Override
   public boolean isEmpty() {
      return cache.isEmpty();
   }

   @Override
   public boolean contains(K key) {
      return cache.containsKey(key);
   }
   
   @Override
   public V cache(K key, V value) {
      return updater.cache(key, value);
   }

   @Override
   public void cache(Map<K, V> values) {
      updater.cache(values);
   }
   
   @Override
   public void clear() {
      updater.clear();
   }

   @Override
   public int size() {
      return cache.size();
   }
   
   @Override
   public String toString() {
      return String.valueOf(cache);
   }
   
   private class MapUpdater {
      
      private final Map<K, V> empty;
      private final float density;
      private final int size;
      
      public MapUpdater(int size, float density) {
         this.empty = new HashMap<K, V>();
         this.density = density;
         this.size = size;
      }

      public synchronized void cache(Map<K, V> values) {
         Map<K, V> copy = new HashMap<K, V>(size, density);
         
         copy.putAll(cache);
         copy.putAll(values);
         cache = copy;
      }
      
      public synchronized V cache(K key, V value) {
         V existing = cache.get(key);
         
         if(existing != value) { // reduce churn
            Map<K, V> copy = new HashMap<K, V>(size, density);
            
            copy.putAll(cache);
            copy.put(key, value);
            cache = copy;
         }
         return existing;
      }
      
      public synchronized V take(K key) {
         V existing = cache.get(key);
         
         if(existing != null) {
            Map<K, V> copy = new HashMap<K, V>(size, density);
            
            copy.putAll(cache);
            copy.remove(key);
            cache = copy;
         }
         return existing;
      }
      
      public synchronized void clear() {
         cache = empty;
      }
   }
}