package org.simpleframework.module.resource.action.build;

import static org.simpleframework.module.resource.action.build.ComponentType.RESOURCE;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.module.build.Function;
import org.simpleframework.module.build.MethodScanner;
import org.simpleframework.module.core.Validator;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Ignore;
import org.simpleframework.module.resource.annotation.Verb;

public class ActionScanner {

   private final MethodPathBuilder builder;
   private final PathFormatter formatter;
   private final MethodScanner scanner;
   private final Validator validator;

   public ActionScanner(MethodScanner scanner, Validator validator) {
      this.builder = new MethodPathBuilder();
      this.formatter = new PathFormatter();
      this.validator = validator;
      this.scanner = scanner;
   }

   public Map<String, List<MethodDispatcher>> createDispatchers(Class<?> type) throws Exception {
      Map<String, List<MethodDispatcher>> dispatchers = new LinkedHashMap<>();

      if (type != null) {
         ComponentType componentType = ComponentType.resolveType(type);
         List<Function> functions = scanner.createMethods(type);
         String typePath = componentType.extractPath(type);
         
         for (Function function : functions) {
            String methodPath = RESOURCE.extractPath(function);

            if (methodPath != null) {
               MethodMatcher matcher = createMatcher(function, typePath, methodPath);
               MethodDispatcher dispatcher = createDispatcher(matcher, function);
               String pattern = matcher.pattern();

               if(dispatcher == null) {
                  throw new IllegalStateException("Could not resolve for " + pattern + " on " + function);
               }
               dispatchers.computeIfAbsent(pattern, PatternList::new).add(dispatcher);
            }
         }
      }
      return dispatchers;
   }
   
   private MethodMatcher createMatcher(Function function, String typePath, String methodPath) throws Exception {
      String methodName = function.getName();
      Ignore ignore = function.getAnnotation(Ignore.class);
      Annotation[] annotations = function.getAnnotations(); 
      String realPath = builder.create(methodPath, methodName);
      String parentPath = "/";
      String ignorePath = "";
      
      if (ignore != null) {
         ignorePath = ignore.value();
         formatter.formatPath(ignorePath);
      }
      if (typePath != null) {
         parentPath = formatter.formatPath(typePath);
      }
      for(Annotation annotation : annotations) {
         Class<? extends Annotation> methodVerb = annotation.annotationType();
         
         if(methodVerb.isAnnotationPresent(Verb.class)) {
            return new MethodMatcher(methodVerb, ignorePath, parentPath, realPath);
         }
      }
      return new MethodMatcher(GET.class, ignorePath, parentPath, realPath);
 
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
            header.extract(annotation);
         }
         return header;
      }
      return null;
   }
   
   private static class PatternList<T> extends ArrayList<T> {
      
      private final String pattern;
      
      public PatternList(String pattern) {
         this.pattern = pattern;
      }
      
      public String pattern() {
         return pattern;
      }
   }
}
