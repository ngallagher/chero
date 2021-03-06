package org.simpleframework.resource.write;

import org.simpleframework.http.Response;

public interface BodyWriter<T> {
   boolean accept(Response response, Object result) throws Exception;
   boolean write(Response response, T result) throws Exception;
}
