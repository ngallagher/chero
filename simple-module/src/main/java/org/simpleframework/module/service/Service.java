package org.simpleframework.module.service;

public interface Service<T> {
   Service<T> register(Object instance);
   T start();
}
