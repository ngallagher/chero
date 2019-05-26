package org.simpleframework.module.resource.server;

import java.util.HashSet;
import java.util.Set;

import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.graph.ClassPath;
import org.simpleframework.module.service.Service;
import org.simpleframework.module.service.ServiceAssembler;

public class ServerBinder {
   
   private final ServiceAssembler assembler;
   private final ComponentManager manager;
   private final ClassPath path;
   private final Set<Class> ignore;
   
   public ServerBinder(ServiceAssembler assembler, ComponentManager manager, ClassPath path) {
      this.ignore = new HashSet<>();
      this.assembler = assembler;
      this.manager = manager;
      this.path = path;
   }
   
   public ServerBinder register(Object instance) {
      Class type = instance.getClass();
      
      ignore.add(type);
      manager.register(instance);
      
      return this;
   }
   
   public ServerBinder start(Context context) {
      Service service = assembler.assemble(path, context, ignore);
      
      service.start();
      ignore.clear();
      
      return this;
      
   }

}
