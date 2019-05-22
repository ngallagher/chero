package org.simpleframework.module.resource.action;

import org.simpleframework.module.context.Context;

public interface Action {
   Object execute(Context context) throws Throwable;
}
