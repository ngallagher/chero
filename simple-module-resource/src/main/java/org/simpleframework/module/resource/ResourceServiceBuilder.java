package org.simpleframework.module.resource;

import java.util.HashSet;
import java.util.Set;

import org.simpleframework.module.ApplicationBuilder;
import org.simpleframework.module.common.DependencyPath;
import org.simpleframework.module.common.DependencyPathBuilder;

public class ResourceServiceBuilder implements ApplicationBuilder<ResourceService> {
   
   private DependencyPathBuilder builder;
   private Set<Class<?>> modules;
   private int port;
   
   public ResourceServiceBuilder() {
      this.modules = new HashSet<>();
      this.builder = new DependencyPathBuilder(modules);
      this.port = 80;
   }
   
   public ResourceServiceBuilder register(Class<?> type) {
      modules.add(type);
      return this;
   }
   
   public ResourceServiceBuilder listen(int port) {
      this.port = port;
      return this;
   }
   
   public ResourceService start() {
      DependencyPath path = builder.create();
      
      try {
         ResourceService service = new ResourceService(path);
         service.start(port);
         return service;
      } catch(Exception e) {
         throw new IllegalStateException("Could not start service", e);
      }
   }
}
