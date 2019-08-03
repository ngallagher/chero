package org.simpleframework.resource.write;

import static org.simpleframework.resource.MediaType.APPLICATION_XML;
import static org.simpleframework.resource.MediaType.TEXT_XML;

import java.io.OutputStream;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Response;
import org.simpleframework.module.extract.StringConverter;
import org.simpleframework.resource.MediaTypeFilter;
import org.simpleframework.xml.Serializer;

public class XmlWriter implements BodyWriter<Object> {

   private final StringConverter converter;
   private final MediaTypeFilter filter;
   private final Serializer serializer;
   
   public XmlWriter(Serializer serializer) {
      this.filter = new MediaTypeFilter(APPLICATION_XML, TEXT_XML);
      this.converter = new StringConverter();
      this.serializer = serializer;
   }
   
   @Override
   public boolean accept(Response response, Object result) throws Exception {
      ContentType type = response.getContentType();
      
      if(type != null && result != null) {
         Class real = result.getClass();
         
         if(filter.accept(type)) {
            return !real.isArray() && !converter.accept(real);
         }
      }
      return false;
   }

   @Override
   public boolean write(Response response, Object result) throws Exception {
      OutputStream output = response.getOutputStream();
      
      if(result != null) {
         serializer.write(result, output);     
      }      
      return true;
   }

}
