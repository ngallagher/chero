package org.simpleframework.module.resource.action;

import java.util.List;
import java.util.Map;

import org.simpleframework.module.build.Argument;

public interface ActionDescription {
   List<Argument> getArguments();
   Map<String, String> getHeaders();
   String getDescription();
   String getMethod();
   String getPath();
}


