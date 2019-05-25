package org.simpleframework.module.resource.action.extract;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.StringConverter;
import org.simpleframework.module.resource.annotation.CookieParam;

public class CookieExtractor implements Extractor<Object> {

   private final StringConverter converter;

   public CookieExtractor() {
      this.converter = new StringConverter();
   }

   @Override
   public Object extract(Argument argument, Context context) {
      CookieParam annotation = argument.getAnnotation(CookieParam.class);
      
      if(annotation != null) {
         Class type = argument.getType();
         Model model = context.getModel();
         Request request = model.get(Request.class);
         
         if(request == null) {
            throw new IllegalStateException("Could not get request from model");
         }
         String name = annotation.value();
         String substitute = argument.getDefault();
         Cookie cookie = request.getCookie(name);
   
         if (cookie != null) {
            String value = cookie.getValue();
            
            if (type == Cookie.class) {
               return cookie;
            }
            return converter.convert(type, value);
         }
         if (substitute != null) {
            return converter.convert(type, substitute);
         }
      }
      return null;
   }

   @Override
   public boolean accept(Argument argument) {
      CookieParam annotation = argument.getAnnotation(CookieParam.class);
      
      if(annotation != null) {
         String name = annotation.value();
         Class type = argument.getType();
   
         if (name != null) {
            if (type == Cookie.class) {
               return true;
            }
            return converter.accept(type);
         }
      }
      return false;
   }
}
