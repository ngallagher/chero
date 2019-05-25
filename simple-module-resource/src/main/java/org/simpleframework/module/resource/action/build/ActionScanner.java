package org.simpleframework.module.resource.action.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.simpleframework.module.build.Function;
import org.simpleframework.module.build.MethodScanner;
import org.simpleframework.module.context.Validator;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Ignore;
import org.simpleframework.module.resource.annotation.Verb;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class ActionScanner {

   private final PathFormatter formatter;
   private final MethodScanner scanner;
   private final Validator validator;

   public ActionScanner(MethodScanner scanner, Validator validator) {
      this.formatter = new PathFormatter();
      this.validator = validator;
      this.scanner = scanner;
   }

   public Multimap<String, MethodDispatcher> createDispatchers(Class<?> type) throws Exception {
      Multimap<String, MethodDispatcher> dispatchers = LinkedHashMultimap.create();

      if (type != null) {
         ComponentType componentType = ComponentType.resolveType(type);
         List<Function> functions = scanner.createMethods(type);
         String typePath = componentType.extractPath(type);
         
         for (Function function : functions) {
            String methodPath = componentType.extractPath(function);

            if (methodPath != null) {
               MethodMatcher matcher = createMatcher(function, typePath, methodPath);
               MethodDispatcher dispatcher = createDispatcher(matcher, function);
               String pattern = matcher.pattern();

               if(dispatcher == null) {
                  throw new IllegalStateException("Could not resolve for " + pattern + " on " + function);
               }
               dispatchers.put(pattern, dispatcher);
            }
         }
      }
      return dispatchers;
   }
   
   private MethodMatcher createMatcher(Function function, String typePath, String methodPath) throws Exception {
      Ignore ignore = function.getAnnotation(Ignore.class);
      Annotation[] annotations = function.getAnnotations();
      String methodName = function.getName();
      String parentPath = "/";
      String ignorePath = "";
      
      if (ignore != null) {
         ignorePath = ignore.value();
         formatter.formatPath(ignorePath);
      }
      if (typePath != null) {
         parentPath = formatter.formatPath(typePath);
      }
      if (!methodPath.equals("") && !methodPath.equals("/")) {
         methodPath = formatter.formatPath(methodPath);  
      } else {
         methodPath = formatter.formatPath(methodName);
      }
      for(Annotation annotation : annotations) {
         Class<? extends Annotation> methodVerb = annotation.annotationType();
         
         if(methodVerb.isAnnotationPresent(Verb.class)) {
            return new MethodMatcher(methodVerb, ignorePath, parentPath, methodPath);
         }
      }
      return new MethodMatcher(GET.class, ignorePath, parentPath, methodPath);
 
   }

   private MethodDispatcher createDispatcher(MethodMatcher matcher, Function function) throws Exception {
      MethodHeader header = createHeader(function);

      if (header != null) {
         return new MethodDispatcher(matcher, header, function);
      }
      return null;
   }

   private MethodHeader createHeader(Function function) throws Exception {
      Annotation[] annotations = function.getDeclaredAnnotations();
      
      if(annotations.length > 0) {
         MethodHeader header = new MethodHeader();
   
         for (Annotation annotation : annotations) {
            header.extractHeader(annotation);
         }
         return header;
      }
      return null;
   }
}
