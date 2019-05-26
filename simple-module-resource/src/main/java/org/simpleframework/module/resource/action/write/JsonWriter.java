package org.simpleframework.module.resource.action.write;

import java.io.PrintStream;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Response;

import com.google.gson.Gson;

public class JsonWriter implements BodyWriter<Object> {
   
   private static final String APPLICATION_JSON = "application/json";
   private static final String TEXT_JSON = "text/json";

   private final Gson gson;
   
   public JsonWriter() {
      this.gson = new Gson();
   }
   
   @Override
   public boolean accept(Response response, Object result) throws Exception {
      ContentType type = response.getContentType();
      
      if(type != null && result != null) {
         String value = type.getType();
         
         if(value.equalsIgnoreCase(APPLICATION_JSON)) {
            return true;
         }
         if(value.equalsIgnoreCase(TEXT_JSON)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean write(Response response, Object result) throws Exception {
      PrintStream output = response.getPrintStream();
      String text = gson.toJson(result);
      
      if(text != null) {
         output.print(text);      
      }      
      return true;
   }

}
