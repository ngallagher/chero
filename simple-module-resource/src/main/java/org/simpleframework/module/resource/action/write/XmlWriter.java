package org.simpleframework.module.resource.action.write;

import static org.simpleframework.module.resource.MediaType.APPLICATION_XML;
import static org.simpleframework.module.resource.MediaType.TEXT_XML;

import java.io.OutputStream;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Response;
import org.simpleframework.xml.Serializer;

public class XmlWriter implements BodyWriter<Object> {

   private final Serializer serializer;
   
   public XmlWriter(Serializer serializer) {
      this.serializer = serializer;
   }
   
   @Override
   public boolean accept(Response response, Object result) throws Exception {
      ContentType type = response.getContentType();
      
      if(type != null && result != null) {
         String value = type.getType();
         
         if(value.equalsIgnoreCase(APPLICATION_XML.value)) {
            return true;
         }
         if(value.equalsIgnoreCase(TEXT_XML.value)) {
            return true;
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
