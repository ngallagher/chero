package org.simpleframework.resource.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import org.simpleframework.resource.annotation.Entity;
import org.simpleframework.resource.annotation.Filter;
import org.simpleframework.resource.annotation.Path;

public enum ComponentType {
   RESOURCE(Path.class) {
      public String extractPath(AnnotatedElement element) {
         Path export = element.getAnnotation(Path.class);

         if (export != null) {
            return export.value();
         }
         return null;
      }
   },
   FILTER(Filter.class) {
      public String extractPath(AnnotatedElement element) {
         Filter export = element.getAnnotation(Filter.class);

         if (export != null) {
            return export.value();
         }
         return null;
      }
   },
   ENTITY(Entity.class) {
      public String extractPath(AnnotatedElement element) {
         return null;
      }
   };

   private final Class type;

   private ComponentType(Class type) {
      this.type = type;
   }

   public abstract String extractPath(AnnotatedElement element);

   public static ComponentType resolveType(Class<?> type) {
      Annotation[] annotations = type.getAnnotations();

      for (ComponentType component : values()) {
         for (Annotation annotation : annotations) {
            Class declaration = annotation.annotationType();

            if (declaration == component.type) {
               return component;
            }
         }
      }
      return null;
   }
}
