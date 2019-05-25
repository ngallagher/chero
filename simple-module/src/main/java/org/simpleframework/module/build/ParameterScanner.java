package org.simpleframework.module.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.util.Map;

public class ParameterScanner {

   private final AnnotationExtractor annotations;
   private final GenericsExtractor generics;
   
   public ParameterScanner() {
      this.annotations = new AnnotationExtractor();
      this.generics = new GenericsExtractor();
   }
   
   public Parameter[] createParameters(Executable executable) throws Exception {
      Annotation[][] annotations = executable.getParameterAnnotations();
      Type[] types = executable.getGenericParameterTypes();
      Class[] classes = executable.getParameterTypes();

      if (types.length > 0) {
         Parameter[] parameters = new Parameter[types.length];

         for (int i = 0; i < types.length; i++) {
            Class[] dependents = generics.extract(types[i]);
            Declaration declaration = createDeclaration(classes[i], dependents, annotations[i]);
            Parameter parameter = createParameter(executable, declaration);
            
            parameters[i] = parameter;
         }
         return parameters;
      }
      return new Parameter[] {};
   }
   
   private Parameter createParameter(Executable executable, Declaration declaration) throws Exception {
      Class[] generics = declaration.getGenerics();
      boolean constructor = Constructor.class.isInstance(executable);
      boolean required = declaration.isRequired();
      int modifiers = 0;
      
      modifiers |= constructor ? Modifier.CONSTRUCTOR.mask : 0;
      modifiers |= required ? Modifier.REQUIRED.mask : 0;
      modifiers |= !constructor ? Modifier.METHOD.mask : 0;
      
      return new Parameter(executable, declaration, generics, modifiers);
   }
   
   private Declaration createDeclaration(Class type, Class[] generics, Annotation[] labels) throws Exception {
      AnnotationContext data = annotations.extract(labels);
      Map<Class, Annotation> map = data.getAnnotations();
      String substitute = data.getDefault();
      boolean required = data.isRequired();

      return new Declaration(map, type, generics, substitute, required);
   }
}
