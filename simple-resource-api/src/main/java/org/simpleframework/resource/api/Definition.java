package org.simpleframework.resource.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Definition {

   private Map<String, Property> properties;
   private List<String> required;
   private String type;

   public Definition() {
      this.properties = new LinkedHashMap<>();
      this.required = new ArrayList<>();
   }

   public Map<String, Property> getProperties() {
      return properties;
   }

   public void setProperties(Map<String, Property> properties) {
      this.properties = properties;
   }

   public List<String> getRequired() {
      return required;
   }

   public void setRequired(List<String> required) {
      this.required = required;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public static class Property {

      private String type;
      private String format;

      public String getType() {
         return type;
      }

      public void setType(String type) {
         this.type = type;
      }

      public String getFormat() {
         return format;
      }

      public void setFormat(String format) {
         this.format = format;
      }

   }
}
