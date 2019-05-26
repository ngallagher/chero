package org.simpleframework.module;

public interface Binder<T> {
   Binder<T> module(Class<?> module);
   Binder<T> path(String path);
   Binder<T> file(String file);
   T create(String... arguments);
}
