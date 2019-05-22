package org.simpleframework.service.reflect;

public interface Accessor {
   <T> T getValue(Object source);
   Class getType();
}