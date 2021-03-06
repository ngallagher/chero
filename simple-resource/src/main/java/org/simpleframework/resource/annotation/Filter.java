package org.simpleframework.resource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Transient
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {
   String value() default "/";
}
