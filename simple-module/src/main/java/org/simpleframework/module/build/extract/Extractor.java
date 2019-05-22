package org.simpleframework.module.build.extract;

import org.simpleframework.module.context.Context;

public interface Extractor<T> {
   T extract(Parameter parameter, Context context) throws Exception;
   boolean accept(Parameter parameter) throws Exception;
}
