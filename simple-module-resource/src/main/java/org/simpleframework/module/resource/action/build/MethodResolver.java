package org.simpleframework.module.resource.action.build;

import org.simpleframework.module.core.Context;

public interface MethodResolver {
   MethodDispatcher resolveBest(Context context) throws Exception;
   Iterable<MethodDispatcher> resolveBestFirst(Context context) throws Exception;
   Iterable<MethodDispatcher> resolveBestLast(Context context) throws Exception;
}
