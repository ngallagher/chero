package org.simpleframework.module.service;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.Driver;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.ComponentStore;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Process;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ValueExtractor;
import org.simpleframework.module.path.ClassPath;

public class ServiceDriver implements Driver<Service> {

   private final ComponentManager manager;
   
   public ServiceDriver() {
      this.manager = new ComponentStore();
   }
   
   @Override
   public Service<Process> create(ClassPath path, Context context) {
      List<Extractor> extractors = new LinkedList<>();
      ServiceAssembler assembler = new ServiceAssembler(manager, extractors, argument -> false);
      ServiceBinder binder = new ServiceBinder(assembler, manager, path);
      Extractor extractor = new ValueExtractor();      
      
      binder.register(path);
      binder.register(context);
      binder.register(manager);
      extractors.add(extractor);

      return new Service<Process>() {               

         @Override
         public Object resolve(Class type) {
            return manager.resolve(type);
         }
         
         @Override
         public Object resolve(Class type, String name) {
            return manager.resolve(type, name);
         }
         
         @Override
         public Service register(Object instance) {
            binder.register(instance);
            return this;
         }

         @Override
         public Process start() {       
            binder.start(context);
            return () -> binder.stop();
         }
      };
   }
}
