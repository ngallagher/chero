package org.simpleframework.module.build.extract;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;

public class Parameter {

   protected final Map<Class, Annotation> annotations;
   protected final String value;
   protected final Class type;
   protected final Class entry;
   protected final boolean required;
   protected final boolean constructor;

   public Parameter(Class type, Class entry, String value, Map<Class, Annotation> annotations, boolean constructor) {
      this(type, entry, value, annotations, constructor, false);
   }

   public Parameter(Class type, Class entry, String value, Map<Class, Annotation> annotations, boolean constructor, boolean required) {
      this.annotations = Collections.unmodifiableMap(annotations);
      this.constructor = constructor;
      this.required = required;
      this.value = value;
      this.entry = entry;
      this.type = type;
   }
   
   public <T extends Annotation> T getAnnotation(Class<T> type) {
      return (T) annotations.get(type);
   }
   
   public boolean isConstructor() {
      return constructor;
   }

   public boolean isRequired() {
      return required || constructor;
   }
   
   public boolean isList() {
      return entry != null;
   }
   
   public Class getEntry() {
      return entry;
   }

   public Class getType() {
      return type;
   }

   public String getDefault() {
      return value;
   }
}
