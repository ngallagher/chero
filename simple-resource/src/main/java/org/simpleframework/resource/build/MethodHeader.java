package org.simpleframework.resource.build;

import static org.simpleframework.http.Protocol.ACCEPT;
import static org.simpleframework.http.Protocol.CACHE_CONTROL;
import static org.simpleframework.http.Protocol.CONTENT_DISPOSITION;
import static org.simpleframework.http.Protocol.CONTENT_TYPE;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Interpolator;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.core.TokenFilter;
import org.simpleframework.module.core.ContextFilter;
import org.simpleframework.resource.MediaTypeMatcher;
import org.simpleframework.resource.annotation.Attachment;
import org.simpleframework.resource.annotation.CacheControl;
import org.simpleframework.resource.annotation.Consumes;
import org.simpleframework.resource.annotation.Produces;

public class MethodHeader {

   private final Map<String, String> headers;
   private final MediaTypeMatcher output;
   private final MediaTypeMatcher input;
   private final List<String> consumes;
   private final List<String> produces;
   private final Set<Class> done;
   
   public MethodHeader() {
      this.consumes = new ArrayList<String>();
      this.produces = new ArrayList<String>();
      this.headers = new LinkedHashMap<String, String>();
      this.input = new MediaTypeMatcher(produces, ACCEPT);
      this.output = new MediaTypeMatcher(consumes, CONTENT_TYPE);
      this.done = new HashSet<Class>();
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
      if(!produces.isEmpty() && !input.accept(request)) {
         return -1;
      }
      if(!consumes.isEmpty() && !output.accept(request)) {
         return -1;
      }
      return 1;
   }

   public void apply(Context context) {
      Set<String> names = headers.keySet();

      if (!names.isEmpty() || !produces.isEmpty()) {
         Model model = context.getModel();
         Response response = model.get(Response.class);
         Request request = model.get(Request.class);
         
         if(response == null) {
            throw new IllegalStateException("Could not get response from model");
         }
         if(request == null) {
            throw new IllegalStateException("Could not get request from model");
         }
         TokenFilter filter = new ContextFilter(context);
         Interpolator interpolator = new Interpolator(filter);

         for (String name : names) {
            String value = headers.get(name);
            String text = interpolator.interpolate(value);

            response.setValue(name, text);
         }
         String type = input.match(request);
         String text = interpolator.interpolate(type);
         
         if(text != null) {
            response.setContentType(text);
         }
      }
   }

   public void extract(Annotation annotation) {
      if(annotation != null) {
         Class type = annotation.annotationType();
         
         if(done.add(type)) {
            if (annotation instanceof Attachment) {
               extract((Attachment) annotation);
            }
            if (annotation instanceof Produces) {
               extract((Produces) annotation);
            }
            if (annotation instanceof Consumes) {
               extract((Consumes) annotation);
            }
            if (annotation instanceof CacheControl) {
               extract((CacheControl) annotation);
            }
         }
      }
   }

   private void extract(Produces annotation) {
      String[] values = annotation.value();

      for(String value : values) {
         String type = value.toLowerCase();
         
         if (!type.isEmpty()) {
            produces.add(type);
         }
      }
   }
   
   private void extract(Consumes annotation) {
      String[] values = annotation.value();

      for(String value : values) {
         String type = value.toLowerCase();
         
         if (!type.isEmpty()) {
            consumes.add(type);
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
