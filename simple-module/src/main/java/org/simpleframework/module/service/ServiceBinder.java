package org.simpleframework.module.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.module.common.Task;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.index.ModuleFilter;
import org.simpleframework.module.path.ClassPath;

public class ServiceBinder {
   
   private final AtomicReference<Task> reference;
   private final ServiceAssembler assembler;
   private final ComponentManager manager;
   private final ModuleFilter filter;
   private final ClassPath path;
   private final Set<Class> ignore;
   
   public ServiceBinder(ServiceAssembler assembler, ComponentManager manager, ClassPath path) {
      this.reference = new AtomicReference<Task>();
      this.ignore = new HashSet<>();
      this.filter = new ModuleFilter(path, ignore);
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
      Task task = assembler.assemble(filter, path, context);
      
      task.start();
      reference.set(task);
      ignore.clear();
      
      return this;
   }
   
   public void stop() {
      Task task = reference.getAndSet(null);
      
      if(task != null) {
         task.stop();
      }
   }
}
