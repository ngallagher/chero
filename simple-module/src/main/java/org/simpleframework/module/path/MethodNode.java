package org.simpleframework.module.path;

import java.util.List;

public interface MethodNode {
   List<ClassNode> getParameterTypes();
   ClassNode getDeclaringClass();
   ClassNode getReturnType();
}
