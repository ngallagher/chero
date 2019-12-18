package org.simpleframework.module.path;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface AnnotationNode {
   Annotation getAnnotation();
   <T extends Annotation> T getAnnotation(Class<T> type);
   Map<String, Object> getValues();
   String getName();
}
