package org.simpleframework.module.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Property implements Argument {

   private final Declaration declaration;
   private final Class[] generics;
   private final Field field;
   private final int modifiers;
   
   public Property(Field field, Declaration declaration, Class[] generics, int modifiers) {
      this.declaration = declaration;
      this.modifiers = modifiers;
      this.generics = generics;
      this.field = field;
   }
   
   @Override
   public <T extends Annotation> T getAnnotation(Class<T> type) {
      return (T) declaration.getAnnotation(type);
   }
   
   @Override
   public Field getSource() {
      return field;
   }
   
   public boolean isInjectable() {
      return Modifier.isInjectable(modifiers);
   }
   
   @Override
   public boolean isConstructor() {
      return Modifier.isConstructor(modifiers);
   }

   @Override
   public boolean isRequired() {
      return Modifier.isRequired(modifiers);
   }
   
   @Override
   public boolean isCollection() {
      return generics.length == 1;
   }
   
   @Override
   public Class getEntry() {
      return generics.length > 0 ? generics[0] : null;
   }

   @Override
   public Class getType() {
      return declaration.getType();
   }

   @Override
   public String getDefault() {
      return declaration.getDefault();
   }
}
