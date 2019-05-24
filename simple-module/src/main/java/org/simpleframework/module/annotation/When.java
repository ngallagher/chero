package org.simpleframework.module.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.simpleframework.module.context.Evaluation;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface When {
   Class<? extends Evaluation> value();
}
