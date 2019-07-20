package org.simpleframework.module.resource.action.build;

import static java.lang.Float.MIN_VALUE;
import static org.simpleframework.http.Protocol.CACHE_CONTROL;
import static org.simpleframework.http.Protocol.CONTENT_DISPOSITION;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Interpolator;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.resource.MediaTypeMatcher;
import org.simpleframework.module.resource.annotation.Attachment;
import org.simpleframework.module.resource.annotation.CacheControl;
import org.simpleframework.module.resource.annotation.Produces;

public class MethodHeader {

   private final Map<String, String> headers;
   private final List<String> types;
   private final MediaTypeMatcher matcher;

   public MethodHeader() {
      this.types = new ArrayList<String>();
      this.headers = new LinkedHashMap<String, String>();
      this.matcher = new MediaTypeMatcher(types);
   }
   
   public Map<String, String> headers() {
      return Collections.unmodifiableMap(headers);
   }
   
   public float score(Context context) {
      Model model = context.getModel();
      Request request = model.get(Request.class);
      
      if(request == null) {
         throw new IllegalStateException("Could not get request from model");
      }
      return matcher.accept(request) == null ? MIN_VALUE : 1;
   }

   public void apply(Context context) {
      Set<String> names = headers.keySet();

      if (!names.isEmpty() || !types.isEmpty()) {
         Model model = context.getModel();
         Response response = model.get(Response.class);
         Request request = model.get(Request.class);
         
         if(response == null) {
            throw new IllegalStateException("Could not get response from model");
         }
         if(request == null) {
            throw new IllegalStateException("Could not get request from model");
         }
         Interpolator interpolator = new Interpolator(context);

         for (String name : names) {
            String value = headers.get(name);
            String text = interpolator.interpolate(value);

            response.setValue(name, text);
         }
         String type = matcher.accept(request);
         String text = interpolator.interpolate(type);
         
         response.setContentType(text);
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

   private void extract(Produces annotation) {
      String[] values = annotation.value();

      for(String value : values) {
         String type = value.toLowerCase();
         
         if (!type.isEmpty()) {
            types.add(type);
         }
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
