package org.simpleframework.module.build;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.extract.Extractor;

public class ConstructorScanner {

   private final Map<Class, List<Function>> cache;
   private final PropertyInjectorBuilder builder;
   private final ArgumentListBuilder arguments;
   private final InstanceManager manager;

   public ConstructorScanner(ComponentManager manager, List<Extractor> extractors, Predicate<Argument> transients) {
      this.cache = new ConcurrentHashMap<Class, List<Function>>();
      this.builder = new PropertyInjectorBuilder(manager, this, extractors, transients);
      this.arguments = new ArgumentListBuilder(manager, this, extractors, transients);
      this.manager = new InstanceManager(manager, transients);
   }
   
   public List<Function> createConstructors(Class type) throws Exception {
      List<Function> builders = cache.get(type);
   
      if(type != null) {
         PropertyInjector injector = builder.createInjector(type);
         Constructor[] factories = type.getDeclaredConstructors();

         if(factories.length > 0) {
            List<Function> matches = new LinkedList<Function>();
               
            for (Constructor factory : factories) {
               Function builder = createConstructor(injector, factory);
   
               if (builder != null) {
                  matches.add(builder);
               }
            }
            if(matches.isEmpty()) {
               throw new IllegalStateException("Could not construct " + type);
            }
            cache.put(type, matches);
            return matches;
         }
         return Collections.emptyList();
      }
      return builders;
   }
   
   private Function createConstructor(PropertyInjector injector, Constructor factory) throws Exception {
      ArgumentList list = arguments.createArguments(factory);
      
      if(list != null) {
         if (!factory.isAccessible()) {
            factory.setAccessible(true);
         }
         return new ConstructorFunction(manager, list, injector, factory);
      }
      return null;
   }
}
