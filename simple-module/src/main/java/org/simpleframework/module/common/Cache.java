package org.simpleframework.module.common;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface Cache<K, V> {
   Set<K> keySet();
   Collection<V> values();
   V take(K key);
   V fetch(K key); 
   V fetch(K key, Function<K, V> builder);
   boolean isEmpty();
   boolean contains(K key);
   V cache(K key, V value);
   void cache(Map<K, V> values);
   void clear();
   int size();
}