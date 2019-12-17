package org.simpleframework.module.path;

import java.util.List;

public interface ConstructorNode {
   boolean isAnnotationPresent(String name);
   List<ParameterNode> getParameters();
   ClassNode getDeclaringClass();
}
