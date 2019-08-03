package org.simpleframework.resource.write;

import static org.simpleframework.resource.MediaType.APPLICATION_JSON;
import static org.simpleframework.resource.MediaType.TEXT_JSON;

import java.io.OutputStream;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Response;
import org.simpleframework.module.extract.StringConverter;
import org.simpleframework.resource.MediaTypeFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonWriter implements BodyWriter<Object> {

   private final StringConverter converter;
   private final MediaTypeFilter filter;
   private final ObjectMapper mapper;
   
   public JsonWriter(ObjectMapper mapper) {
      this.filter = new MediaTypeFilter(APPLICATION_JSON, TEXT_JSON);
      this.converter = new StringConverter();
      this.mapper = mapper;
   }
   
   @Override
   public boolean accept(Response response, Object result) throws Exception {
      ContentType type = response.getContentType();
      
      if(type != null && result != null) {
         Class real = result.getClass();
         
         if(filter.accept(type)) {
            return !converter.accept(real);
         }
      }
      return false;
   }

   @Override
   public boolean write(Response response, Object result) throws Exception {
      OutputStream output = response.getOutputStream();
      
      if(result != null) {
         mapper.writeValue(output, result);     
      }      
      return true;
   }

}
