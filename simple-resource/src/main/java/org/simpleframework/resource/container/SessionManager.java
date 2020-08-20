package org.simpleframework.resource.container;

import java.util.UUID;
import java.util.function.Supplier;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public class SessionManager {

   private final Supplier<String> generator;
   private final String session;

   public SessionManager(String session) {
      this.generator = () -> UUID.randomUUID().toString();
      this.session = session;
   }

   public String resolve(Request request, Response response) {
      if(session != null) {
         Cookie cookie = request.getCookie(session);

         if (cookie == null) {
            String value = generator.get();

            response.setCookie(session, value);
            return value;
         }
         return cookie.getValue();
      }
      return null;
   }
}
