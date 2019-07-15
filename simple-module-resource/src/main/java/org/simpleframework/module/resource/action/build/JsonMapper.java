package org.simpleframework.module.resource.action.build;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.GETTER;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper extends ObjectMapper {
      
   public JsonMapper() {
      configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
      configure(FAIL_ON_EMPTY_BEANS, false);
      setVisibility(GETTER, PUBLIC_ONLY);
   }
}
