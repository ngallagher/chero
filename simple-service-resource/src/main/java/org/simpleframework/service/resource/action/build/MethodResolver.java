package org.simpleframework.service.resource.action.build;

import java.util.List;

import org.simpleframework.service.context.Context;

public interface MethodResolver {
   MethodDispatcher resolveBest(Context context) throws Exception;
   List<MethodDispatcher> resolveBestFirst(Context context) throws Exception;
   List<MethodDispatcher> resolveBestLast(Context context) throws Exception;
}
