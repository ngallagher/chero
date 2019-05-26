package org.simpleframework.module.service;

import java.util.HashSet;
import java.util.Set;

import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.graph.index.ClassPath;

public class ServiceBinder {
   
   private final ServiceAssembler assembler;
   private final ComponentManager manager;
   private final ClassPath path;
   private final Set<Class> ignore;
   
   public ServiceBinder(ServiceAssembler assembler, ComponentManager manager, ClassPath path) {
      this.ignore = new HashSet<>();
      this.assembler = assembler;
      this.manager = manager;
      this.path = path;
   }
   
   public ServiceBinder register(Object instance) {
      Class type = instance.getClass();
      
      ignore.add(type);
      manager.register(instance);
      
      return this;
   }
   
   public ServiceBinder start(Context context) {
      Runnable task = assembler.assemble(path, context, ignore);
      
      task.run();
      ignore.clear();
      
      return this;
      
   }

}
