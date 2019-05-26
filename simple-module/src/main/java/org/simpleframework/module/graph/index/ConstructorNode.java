package org.simpleframework.module.graph.index;

import java.util.List;

public interface ConstructorNode {
   List<ClassNode> getParameterTypes();
   ClassNode getDeclaringClass();
}
