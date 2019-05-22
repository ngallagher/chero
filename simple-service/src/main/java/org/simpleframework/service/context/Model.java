package org.simpleframework.service.context;

public interface Model extends Iterable<Object> {
   <T> T remove(Object key);
   <T> T get(Object key);
   boolean contains(Object key);
   void set(Object key, Object object);
   boolean isEmpty();
}
