package org.simpleframework.service.build;

import org.simpleframework.service.context.Context;

public interface ComponentBuilder {
   Class[] require() throws Exception;
   <T> T build(Context context) throws Exception;
   float score(Context context) throws Exception;
}
