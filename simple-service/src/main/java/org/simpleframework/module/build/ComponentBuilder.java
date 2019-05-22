package org.simpleframework.module.build;

import org.simpleframework.module.context.Context;

public interface ComponentBuilder {
   Class[] require() throws Exception;
   <T> T build(Context context) throws Exception;
   float score(Context context) throws Exception;
}
