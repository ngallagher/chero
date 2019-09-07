package org.simpleframework.module.path;

public interface FieldNode {
   boolean isAnnotationPresent(String name);
   ClassNode getDeclaringClass();
   ClassNode getType();
   String getName();
}
