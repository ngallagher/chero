package org.simpleframework.module;

import org.simpleframework.module.core.Context;
import org.simpleframework.module.graph.index.ClassPath;

public interface Driver<T> {
   T create(ClassPath path, Context context) throws Exception;
}
