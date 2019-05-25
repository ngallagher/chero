package org.simpleframework.module.build;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;

public class Declaration {
   
   private final Map<Class, Annotation> annotations;
   private final String substitute;
   private final Class[] generics;
   private final Class type;
   private final boolean required;

   public Declaration(Map<Class, Annotation> annotations, Class type, Class[] generics, String substitute, boolean required) {
      this.annotations = Collections.unmodifiableMap(annotations);
      this.substitute = substitute;
      this.generics = generics;
      this.required = required;
      this.type = type;
   }
   
   public boolean isRequired() {
      return required;
   }
   
   public <T extends Annotation> T getAnnotation(Class<T> type) {
      return (T)annotations.get(type);
   }

   public Class getType() {
      return type;
   }
   
   public Class[] getGenerics() {
      return generics;
   }
   
   public String getDefault() {
      return substitute;
   }
}
