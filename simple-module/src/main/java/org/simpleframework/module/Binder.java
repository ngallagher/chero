package org.simpleframework.module;

public interface Binder<T> {
   Binder<T> register(Class<?> module);
   Binder<T> register(Class<?>... modules);
   Binder<T> path(String path);
   Binder<T> file(String file);
   T create(String... arguments);
}
