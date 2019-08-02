package org.simpleframework.module.service;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.simpleframework.module.build.Argument;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.core.ComponentListener;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.graph.DependencyProvider;
import org.simpleframework.module.graph.DependencyScanner;
import org.simpleframework.module.index.ModuleFilter;
import org.simpleframework.module.path.ClassPath;

public class ServiceAssembler {
   
   private final DependencyScanner calculator;
   private final DependencyProvider provider;
   private final ComponentManager manager;
   
   public ServiceAssembler(ComponentManager manager, List<Extractor> extractors, Predicate<Argument> filter) {
      this.provider = new DependencyProvider(manager, extractors, filter);
      this.calculator = new DependencyScanner();
      this.manager = manager;
   }

   public Runnable assemble(ModuleFilter filter, ClassPath path, Context context) {
      calculator.create(filter, path).traverse(type -> {
         try {
            List<Function> builders = provider.createProviders(filter, path, type);
            Iterator<Function> iterator = builders.iterator();
            
            while(iterator.hasNext()) {
               try {
                  Function builder = iterator.next();
                  Object instance = builder.getValue(context); 
                  
                  if(instance != null) {
                     manager.register(instance);
                     return instance;
                  }
               } catch(Exception e) {}
            }
         }catch(Exception e) {
            throw new IllegalStateException("Could not start application", e);
         }
         return null;
      });
      return () -> manager.resolveAll(ComponentListener.class)
            .forEach(ComponentListener::onReady);

   }
}
