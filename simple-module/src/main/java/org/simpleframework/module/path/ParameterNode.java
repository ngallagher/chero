package org.simpleframework.module.path;

import java.util.List;

public interface ParameterNode {
   boolean isAnnotationPresent(String name);
   List<AnnotationNode> getAnnotations();
   ClassNode getDeclaringClass();
   ClassNode getType();
   String getName();
}
