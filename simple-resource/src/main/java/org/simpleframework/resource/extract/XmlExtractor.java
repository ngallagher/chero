package org.simpleframework.resource.extract;

import static org.simpleframework.resource.MediaType.APPLICATION_XML;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.StringConverter;
import org.simpleframework.resource.MediaTypeFilter;
import org.simpleframework.resource.annotation.Body;
import org.simpleframework.xml.Serializer;

public class XmlExtractor implements Extractor<Object> {
   
   private final StringConverter converter;
   private final MediaTypeFilter filter;
   private final Serializer serializer;
   private final Extractor extractor;
   
   public XmlExtractor(Serializer serializer) {
      this.filter = new MediaTypeFilter(APPLICATION_XML);
      this.converter = new StringConverter();
      this.extractor = new BodyExtractor();
      this.serializer = serializer;
   }

   @Override
   public float score(Argument argument, Context context) {
      Body annotation = argument.getAnnotation(Body.class);

      if(annotation != null) {
         try {
            return extract(argument, context) == null ? -1 : 2;
         } catch (Exception e) {
            return -1;
         }
      }
      return 0;
   }

   @Override
   public Object extract(Argument argument, Context context) throws Exception {
      Body annotation = argument.getAnnotation(Body.class);
      
      if(annotation != null) {
         Model model = context.getModel();
         Request request = model.get(Request.class);
         Response response = model.get(Response.class);
         
         if(request == null || response == null) {
            throw new IllegalStateException("Could not get request or response from model");
         }   
         ContentType type = request.getContentType();
         
         if(filter.accept(type)) {
            String body = request.getContent();
            Class require = argument.getType();
            
            return serializer.read(require, body);
         }
      }
      return null;
   }

   @Override
   public boolean accept(Argument argument) throws Exception {
      Body annotation = argument.getAnnotation(Body.class);
      
      if(annotation != null) {
         Class require = argument.getType();
         
         if(!converter.accept(require)) {
            return !extractor.accept(argument);
         }
      }
      return false;
   }
}
