package org.simpleframework.module.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

public interface Argument {
   <T extends Annotation> T getAnnotation(Class<T> type);
   boolean isConstructor();
   boolean isRequired();
   boolean isCollection();
   Member getSource();
   String getDefault();
   Class getEntry();
   Class getType();
}
