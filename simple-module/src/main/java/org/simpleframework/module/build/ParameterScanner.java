package org.simpleframework.module.build;

import static org.simpleframework.module.build.Modifier.CONSTRUCTOR;
import static org.simpleframework.module.build.Modifier.METHOD;
import static org.simpleframework.module.build.Modifier.REQUIRED;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.util.Map;

import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.common.Introspector;

public class ParameterScanner {

   private final AnnotationExtractor annotations;
   
   public ParameterScanner() {
      this.annotations = new AnnotationExtractor();
   }
   
   public Parameter[] createParameters(Executable executable) throws Exception {
      Annotation[][] annotations = executable.getParameterAnnotations();
      Class[] types = executable.getParameterTypes();

      if (types.length > 0) {
         Parameter[] parameters = new Parameter[types.length];

         for (int i = 0; i < types.length; i++) {
            Class[] dependents = Introspector.getParameterDependents(executable, i);
            Declaration declaration = createDeclaration(types[i], dependents, annotations[i]);
            Parameter parameter = createParameter(executable, declaration);
            
            parameters[i] = parameter;
         }
         return parameters;
      }
      return new Parameter[] {};
   }
   
   private Parameter createParameter(Executable executable, Declaration declaration) throws Exception {
      Class[] generics = declaration.getGenerics();
      boolean provider = executable.isAnnotationPresent(Provides.class);
      boolean constructor = Constructor.class.isInstance(executable);
      boolean required = declaration.isRequired();
      int modifiers = 0;
      
      modifiers |= constructor ? CONSTRUCTOR.mask | REQUIRED.mask : 0;
      modifiers |= provider ? CONSTRUCTOR.mask | REQUIRED.mask : 0;
      modifiers |= required ? REQUIRED.mask : 0;
      modifiers |= !constructor ? METHOD.mask : 0;
      
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
