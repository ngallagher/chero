package org.simpleframework.module.resource.action.build;

import static org.simpleframework.http.Protocol.CACHE_CONTROL;
import static org.simpleframework.http.Protocol.CONTENT_DISPOSITION;
import static org.simpleframework.http.Protocol.CONTENT_TYPE;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Response;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Interpolator;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.resource.annotation.Attachment;
import org.simpleframework.module.resource.annotation.CacheControl;
import org.simpleframework.module.resource.annotation.Produces;

public class MethodHeader {

   private final Map<String, String> headers;

   public MethodHeader() {
      this.headers = new LinkedHashMap<String, String>();
   }
   
   public Map<String, String> headers() {
      return Collections.unmodifiableMap(headers);
   }

   public void apply(Context context) {
      Set<String> names = headers.keySet();

      if (!names.isEmpty()) {
         Model model = context.getModel();
         Response response = model.get(Response.class);
         
         if(response == null) {
            throw new IllegalStateException("Could not get response from model");
         }
         Interpolator interpolator = new Interpolator(context);

         for (String name : names) {
            String value = headers.get(name);
            String text = interpolator.interpolate(value);

            response.setValue(name, text);
         }
      }
   }

   public void extract(Annotation annotation) {
      if (annotation instanceof Attachment) {
         extract((Attachment) annotation);
      }
      if (annotation instanceof Produces) {
         extract((Produces) annotation);
      }
      if (annotation instanceof CacheControl) {
         extract((CacheControl) annotation);
      }
   }

   private void extract(Produces type) {
      String value = type.value();

      if (!value.isEmpty()) {
         headers.put(CONTENT_TYPE, value);
      }
   }

   private void extract(CacheControl control) {
      String type = control.value();

      if (!type.isEmpty()) {
         headers.put(CACHE_CONTROL, type);
      }
   }

   private void extract(Attachment disposition) {
      String type = disposition.value();

      if (!type.isEmpty()) {
         headers.put(CONTENT_DISPOSITION, "attachment; filename=\"" + type + "\"");
      }
   }
}
