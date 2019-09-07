package org.simpleframework.module.reflect;

public interface Accessor {
   <T> T getValue(Object source);
   String getName();
   Class getType();
}