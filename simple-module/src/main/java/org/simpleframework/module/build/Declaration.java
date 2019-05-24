package org.simpleframework.module.build;

import java.lang.annotation.Annotation;
import java.util.Map;

public class Declaration {
   
   private final Map<Class, Annotation> annotations;
   private final String substitute;
   private final Class[] generics;
   private final Class type;
   
   public Declaration(Map<Class, Annotation> annotations, Class type, Class[] generics) {
      this(annotations, type, generics, null);
   }
   
   public Declaration(Map<Class, Annotation> annotations, Class type, Class[] generics, String substitute) {
      this.annotations = annotations;
      this.substitute = substitute;
      this.generics = generics;
      this.type = type;
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
