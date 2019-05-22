package org.simpleframework.service.resource.action;

import org.simpleframework.service.context.Context;

public interface Action {
   Object execute(Context context) throws Throwable;
}
