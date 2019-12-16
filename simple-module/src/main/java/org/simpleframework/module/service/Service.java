package org.simpleframework.module.service;

public interface Service<S>{
   Service<S> register(Object instance);
   <T> T resolve(Class<T> type);
   <T> T resolve(Class<T> type, String name);
   S start();
}
