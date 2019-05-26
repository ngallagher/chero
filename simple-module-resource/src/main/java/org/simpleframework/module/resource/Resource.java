package org.simpleframework.module.resource;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public interface Resource {
   boolean handle(Request request, Response response) throws Throwable;

}