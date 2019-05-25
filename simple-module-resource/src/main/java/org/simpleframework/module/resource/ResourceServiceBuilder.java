package org.simpleframework.module.resource;

import java.util.HashSet;
import java.util.Set;

import org.simpleframework.module.ApplicationBuilder;
import org.simpleframework.module.argument.ContextBuilder;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.graph.DependencyPath;
import org.simpleframework.module.graph.DependencyPathBuilder;

public class ResourceServiceBuilder implements ApplicationBuilder<ResourceService> {
   
   private DependencyPathBuilder builder;
   private ContextBuilder loader;
   private Set<Class<?>> modules;
   private String[] arguments;
   private int port;
   
   public ResourceServiceBuilder(String... paths) {
      this.modules = new HashSet<>();
      this.loader = new ContextBuilder(paths);
      this.builder = new DependencyPathBuilder(modules);
      this.arguments = new String[] {};
      this.port = 80;
   }
   
   public ResourceServiceBuilder register(Class<?> type) {
      modules.add(type);
      return this;
   }
   
   public ResourceServiceBuilder arguments(String[] arguments) {
      this.arguments = arguments;
      return this;
   }
   
   public ResourceServiceBuilder listen(int port) {
      this.port = port;
      return this;
   }
   
   public ResourceService start() {
      long start = System.currentTimeMillis();
      DependencyPath path = builder.create();
      Context context = loader.create(arguments);
      
      try {
         ResourceService service = new ResourceService(path);
         service.start(context, port);
         return service;
      } catch(Exception e) {
         throw new IllegalStateException("Could not start service", e);
      } finally {
         System.err.println("######## TOTAL TIME "+ (System.currentTimeMillis()-start));
      }
   }
}
