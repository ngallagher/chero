package org.simpleframework.module.path;

import java.util.List;

public interface MethodNode {
   boolean isAnnotationPresent(String name);  
   List<AnnotationNode> getAnnotations();
   List<ParameterNode> getParameters();
   ClassNode getDeclaringClass();
   ClassNode getReturnType();
   String getName();
}
