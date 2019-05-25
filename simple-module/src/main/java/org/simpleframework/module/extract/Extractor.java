package org.simpleframework.module.extract;

import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;

public interface Extractor<T> {
   T extract(Argument argument, Context context) throws Exception;
   boolean accept(Argument argument) throws Exception;
}
