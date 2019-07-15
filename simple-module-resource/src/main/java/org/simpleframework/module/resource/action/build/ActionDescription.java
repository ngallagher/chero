package org.simpleframework.module.resource.action.build;

import java.util.List;
import java.util.Map;

import org.simpleframework.module.build.Parameter;

public interface ActionDescription {
   List<Parameter> getParameters();
   Map<String, String> getHeaders();
   String getDescription();
   String getMethod();
   String getPath();
}


