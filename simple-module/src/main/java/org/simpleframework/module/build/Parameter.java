package org.simpleframework.module.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;

public class Parameter implements Argument {

   private final Declaration declaration;
   private final Executable executable;
   private final Class[] generics;
   private final int modifiers;
   
   public Parameter(Executable executable, Declaration declaration, Class[] generics, int modifiers) {
      this.declaration = declaration;
      this.executable = executable;
      this.modifiers = modifiers;
      this.generics = generics;
   }
   
   @Override
   public <T extends Annotation> T getAnnotation(Class<T> type) {
      return (T) declaration.getAnnotation(type);
   }
   
   @Override
   public Executable getSource() {
      return executable;
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
