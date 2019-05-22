package org.simpleframework.service.build.extract;

import org.simpleframework.service.context.Context;

public interface Extractor<T> {
   T extract(Parameter parameter, Context context) throws Exception;
   boolean accept(Parameter parameter) throws Exception;
}
