package org.simpleframework.resource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Verb
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DELETE {

}