package org.simpleframework.module.build;

import java.lang.reflect.AnnotatedElement;

import org.simpleframework.module.context.Context;

public interface Function extends AnnotatedElement {
   <T> T getValue(Context context) throws Exception;
   float getScore(Context context) throws Exception;
   Class[] getParameters() throws Exception;
   String getName();
}
