package org.simpleframework.module.build;

import java.lang.reflect.AnnotatedElement;

import org.simpleframework.module.core.Context;

public interface Function extends AnnotatedElement {
   <T> T getValue(Context context) throws Exception;
   float getScore(Context context) throws Exception;
   Parameter[] getParameters();
   String getName();
}
