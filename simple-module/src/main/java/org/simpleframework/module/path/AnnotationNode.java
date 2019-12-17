package org.simpleframework.module.path;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface AnnotationNode {
   <T extends Annotation> T getAnnotation(Class<T> type);
   Map<String, Object> getValues();
   String getName();
}
