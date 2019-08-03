package org.simpleframework.resource.action;

import org.simpleframework.http.Cookie;
import org.simpleframework.resource.MediaType;

public interface ResponseBuilder {
   ResponseBuilder header(String header, String value);
   ResponseBuilder cookie(String name, String value);
   ResponseBuilder coookie(Cookie cookie);
   ResponseBuilder type(String type);
   ResponseBuilder type(MediaType type);
   ResponseBuilder entity(Object value);
   ResponseEntity create();
}
