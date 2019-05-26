package org.simpleframework.module.resource;

import java.util.HashSet;
import java.util.Set;

import org.simpleframework.module.build.Service;
import org.simpleframework.module.build.ServiceAssembler;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.graph.ClassPath;

public class ResourceBinder {
   
   private final ServiceAssembler assembler;
   private final ComponentManager manager;
   private final ClassPath path;
   private final Set<Class> ignore;
   
   public ResourceBinder(ServiceAssembler assembler, ComponentManager manager, ClassPath path) {
      this.ignore = new HashSet<>();
      this.assembler = assembler;
      this.manager = manager;
      this.path = path;
   }
   
   public ResourceBinder register(Object instance) {
      Class type = instance.getClass();
      
      ignore.add(type);
      manager.register(instance);
      
      return this;
   }
   
   public ResourceBinder start(Context context) {
      Service service = assembler.assemble(path, context, ignore);
      
      service.start();
      ignore.clear();
      
      return this;
      
   }

}
