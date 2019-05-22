package org.simpleframework.module.build;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.build.extract.Parameter;

public class Property extends Parameter {

   public Property(Class type, Class entry, String value, Map<Class, Annotation> annotations) {
      this(type, entry, value, annotations, false);
   }

   public Property(Class type, Class entry, String value, Map<Class, Annotation> annotations, boolean required) {
      super(type, entry, value, annotations, required);
   }
   
   public boolean isInjectable() {
      return annotations.keySet()
            .stream()
            .filter(Objects::nonNull)
            .anyMatch(type -> type.isAnnotationPresent(Inject.class));
   }
}
