package org.simpleframework.service.resource.action;

import org.simpleframework.service.context.Context;

public interface ActionResolver {
   Action resolve(Context context) throws Exception;
}
