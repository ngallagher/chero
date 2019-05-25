package org.simpleframework.module.resource.action;

import org.simpleframework.module.core.Context;

public interface ActionResolver {
   Action resolve(Context context) throws Exception;
}
