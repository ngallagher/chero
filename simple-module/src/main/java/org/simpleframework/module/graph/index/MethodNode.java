package org.simpleframework.module.graph.index;

import java.util.List;

public interface MethodNode {
   List<ClassNode> getParameterTypes();
   ClassNode getDeclaringClass();
   ClassNode getReturnType();
}
