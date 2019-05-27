package org.simpleframework.module.resource;

public enum MediaType {
   APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
   APPLICATION_HTML_XML("application/html+xml"),
   APPLICATION_JSON("application/json"),
   APPLICATION_OCTET_STREAM("application/octet-stream"),
   APPLICATION_XML("application/xml"),
   MULTIPART_FORM_DATA("multipart/form-data"),
   IMAGE_PNG("image/png"),
   IMAGE_JPEG("image/jpeg"),
   IMAGE_GIF("image/gif"),
   TEXT_CSV("text/csv"),
   TEXT_CSS("text/css"),
   TEXT_JAVASCRIPT("text/javascript"),
   TEXT_JSON("text/json"),
   TEXT_HTML("text/html"),
   TEXT_PLAIN("text/plain"),
   TEXT_XML("text/xml"),
   ALL_ALL("*/*");
   
   public final String value;
   
   private MediaType(String value) {
      this.value = value;
   }
   
   public String getValue() {
      return value;
   }
}
