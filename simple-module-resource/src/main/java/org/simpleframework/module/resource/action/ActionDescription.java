package org.simpleframework.module.resource.action;

import java.util.List;
import java.util.Map;

import org.simpleframework.module.build.Argument;

public interface ActionDescription {
   Map<String, String> getHeaders();
   List<Argument> getArguments();
   String getDescription();
   String getMethod();
   String getPath();
}


