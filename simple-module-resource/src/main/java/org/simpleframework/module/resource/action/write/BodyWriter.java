package org.simpleframework.module.resource.action.write;

import org.simpleframework.http.Response;

public interface BodyWriter<T> {
   boolean accept(Response response, Object result) throws Exception;
   void write(Response response, T result) throws Exception;
}
