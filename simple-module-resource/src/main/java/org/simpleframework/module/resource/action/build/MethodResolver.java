package org.simpleframework.module.resource.action.build;

import java.util.List;

import org.simpleframework.module.core.Context;

public interface MethodResolver {
   MethodDispatcher resolveBest(Context context) throws Exception;
   List<MethodDispatcher> resolveBestFirst(Context context) throws Exception;
   List<MethodDispatcher> resolveBestLast(Context context) throws Exception;
}
