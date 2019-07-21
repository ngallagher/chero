package org.simpleframework.module.resource.action.build;

import static org.simpleframework.module.resource.action.build.ComponentType.RESOURCE;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.module.build.Function;
import org.simpleframework.module.build.MethodScanner;
import org.simpleframework.module.build.Parameter;
import org.simpleframework.module.core.Validator;
import org.simpleframework.module.resource.annotation.Consumes;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Produces;
import org.simpleframework.module.resource.annotation.Verb;

public class ActionScanner {

   private final PathPatternBuilder builder;
   private final MethodScanner scanner;
   private final Validator validator;

   public ActionScanner(MethodScanner scanner, Validator validator) {
      this.builder = new PathPatternBuilder();
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
      Annotation[] annotations = function.getAnnotations(); 
      MethodCompiler compiler = createCompiler(function, typePath, methodPath);
      
      for(Annotation annotation : annotations) {
         Class<? extends Annotation> verb = annotation.annotationType();
         
         if(verb.isAnnotationPresent(Verb.class)) {
            return compiler.compile(verb);
         }
      }
      return compiler.compile(GET.class);
   }
   
   private MethodCompiler createCompiler(Function function, String typePath, String methodPath) throws Exception {
      Parameter[] parameters = function.getParameters();
      PathPattern pattern = builder.createPattern(function, typePath, methodPath);
      
      return new MethodCompiler(pattern, parameters);
   }

   private MethodDispatcher createDispatcher(MethodMatcher matcher, Function function) throws Exception {
      MethodHeader header = createHeader(function);

      if (header != null) {
         return new MethodDispatcher(matcher, header, function);
      }
      return null;
   }

   private MethodHeader createHeader(Function function) throws Exception {
      MethodHeader header = new MethodHeader();
      
      if(function != null) {
         Annotation[] annotations = function.getDeclaredAnnotations();
         Consumes consumes = function.getTypeAnnotation(Consumes.class);
         Produces produces = function.getTypeAnnotation(Produces.class);
         
         for (Annotation annotation : annotations) {
            header.extract(annotation);
         }
         if(produces != null) {
            header.extract(produces);
         }
         if(consumes != null) {
            header.extract(consumes);
         }
      }
      return header;
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
