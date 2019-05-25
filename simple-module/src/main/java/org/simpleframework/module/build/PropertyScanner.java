package org.simpleframework.module.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.common.Introspector;

public class PropertyScanner {

   private final AnnotationExtractor annotations;
   
   public PropertyScanner() {
      this.annotations = new AnnotationExtractor();
   }
   
   public Property createProperty(Field field) throws Exception {
      Class parent = field.getType();
      Annotation[] annotations = field.getAnnotations();
      Class[] dependents = Introspector.getDependents(field);
      Declaration declaration = createDeclaration(parent, dependents, annotations);
      
      return createProperty(field, declaration, annotations);
   }
   
   private Property createProperty(Field field, Declaration declaration, Annotation[] labels) throws Exception {
      Class[] generics = declaration.getGenerics();
      boolean required = declaration.isRequired();
      int modifiers = required ? Modifier.REQUIRED.mask : 0;
      
      for(Annotation label : labels) {
         Class<?> type = label.annotationType();
         
         if(type.isAnnotationPresent(Inject.class)) {
            modifiers |= Modifier.INJECTABLE.mask;
         }
      }
      return new Property(field, declaration, generics, modifiers);
   }
   
   private Declaration createDeclaration(Class type, Class[] generics, Annotation[] labels) throws Exception {
      AnnotationContext data = annotations.extract(labels);
      Map<Class, Annotation> map = data.getAnnotations();
      String substitute = data.getDefault();
      boolean required = data.isRequired();

      return new Declaration(map, type, generics, substitute, required);
   }
}
