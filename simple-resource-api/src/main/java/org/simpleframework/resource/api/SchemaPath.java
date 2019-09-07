package org.simpleframework.resource.api;

import java.util.List;
import java.util.Map;

public class SchemaPath {

   private Map<String, SchemaResponse> responses;
   private List<SchemaParameter> parameters;
   private String description;
   private String operationId;
}
