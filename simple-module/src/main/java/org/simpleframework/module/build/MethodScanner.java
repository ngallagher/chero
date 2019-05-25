package org.simpleframework.module.build;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.simpleframework.module.core.AnnotationValidator;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Validator;
import org.simpleframework.module.extract.Extractor;

public class MethodScanner {

   private final Map<Class, List<Function>> cache;
   private final ArgumentListBuilder arguments;
   private final ConstructorScanner scanner;
   private final ComponentManager manager;
   private final Validator validator;

   public MethodScanner(ComponentManager manager, ConstructorScanner scanner, List<Extractor> extractors, Predicate<Argument> transients) {
      this.cache = new ConcurrentHashMap<Class, List<Function>>();
      this.validator = new AnnotationValidator();
      this.arguments = new ArgumentListBuilder(manager, scanner, extractors, transients);
      this.scanner = scanner;
      this.manager = manager;
   }
   
   public List<Function> createMethods(Class type) throws Exception {
      List<Function> builders = cache.get(type);
      Class base = type;
      
      if(builders == null) {
         List<Function> matches = new LinkedList<Function>();
      
         while(type != null && type != Object.class) {
            Method[] methods = type.getDeclaredMethods();
   
            for (Method method : methods) {
               Function builder = createMethod(type, method);
   
               if (builder != null) {
                  matches.add(builder);
               }
            }
            if(matches.isEmpty()) {
               throw new IllegalStateException("Could not construct " + type);
            }
            type = type.getSuperclass();
         }
         cache.put(base, matches);
         return matches;
      }
      return builders;
   }
   
   private Function createMethod(Class type, Method method) throws Exception {
      ArgumentList list = arguments.createArguments(method);
      
      if(list != null) {
         if (!method.isAccessible()) {
            method.setAccessible(true);
         }
         return new MethodFunction(manager, scanner, list, method, type);
      }
      return null;
   }
}
