package org.simpleframework.module.path;

import java.util.List;

public interface MethodNode {
   boolean isAnnotationPresent(String name);
   List<ClassNode> getParameterTypes();
   ClassNode getDeclaringClass();
   ClassNode getReturnType();
}
