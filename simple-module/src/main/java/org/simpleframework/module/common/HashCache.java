package org.simpleframework.module.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class HashCache<K, V> implements Cache<K, V> {

   private final Map<K, V> cache;

   public HashCache() {
      this.cache = new HashMap<K, V>();
   }

   @Override
   public synchronized void clear() {
      cache.clear();
   }

   @Override
   public synchronized int size() {
      return cache.size();
   }

   @Override
   public synchronized Set<K> keySet() {
      return cache.keySet();
   }

   @Override
   public synchronized Collection<V> values() {
      return cache.values();
   }
   
   @Override
   public synchronized V fetch(K key) {
      return cache.get(key);
   }

   @Override
   public synchronized V fetch(K key, Function<K, V> builder) {
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
   public synchronized V cache(K key, V value) {
      return cache.put(key, value);
   }
   
   @Override
   public synchronized void cache(Map<K, V> values) {
      cache.putAll(values);
   }
   
   @Override
   public synchronized V take(K key) {
      return cache.remove(key);
   }

   @Override
   public synchronized boolean contains(K key) {
      return cache.containsKey(key);
   }

   @Override
   public synchronized boolean isEmpty() {
      return cache.isEmpty();
   }
   
   @Override
   public synchronized String toString() {
      return String.valueOf(cache);
   }
}