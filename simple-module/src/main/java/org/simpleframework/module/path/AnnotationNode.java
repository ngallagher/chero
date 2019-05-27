package org.simpleframework.module.path;

import java.util.Map;

public interface AnnotationNode {
   Map<String, Object> getValues();
   String getName();
}
