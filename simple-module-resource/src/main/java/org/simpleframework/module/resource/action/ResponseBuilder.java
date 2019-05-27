package org.simpleframework.module.resource.action;

import org.simpleframework.http.Cookie;

public interface ResponseBuilder {
   ResponseBuilder header(String header, String value);
   ResponseBuilder cookie(String name, String value);
   ResponseBuilder coookie(Cookie cookie);
   ResponseBuilder type(String type);
   ResponseBuilder entity(Object value);
   ResponseEntity create();
}
