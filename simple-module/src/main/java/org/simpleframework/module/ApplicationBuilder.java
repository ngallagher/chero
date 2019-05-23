package org.simpleframework.module;

public interface ApplicationBuilder<T> {
   ApplicationBuilder<T> register(Class<?> type);
   T start();
}
