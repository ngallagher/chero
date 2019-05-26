package org.simpleframework.module;

import org.simpleframework.module.core.Context;
import org.simpleframework.module.graph.ClassPath;

public interface Application<T> {
   T create(ClassPath path, Context context) throws Exception;
}
