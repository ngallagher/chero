package org.simpleframework.module.path;

import java.util.List;

public interface ConstructorNode {
   List<ClassNode> getParameterTypes();
   ClassNode getDeclaringClass();
}
