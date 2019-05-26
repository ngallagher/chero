package org.simpleframework.module.resource;

import java.io.IOException;

import org.simpleframework.module.build.ServiceAssembler;
import org.simpleframework.module.common.ThreadPool;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.graph.ClassPath;
import org.simpleframework.module.resource.action.ActionAssembler;
import org.simpleframework.module.resource.container.ContainerServer;
import org.simpleframework.module.resource.template.StringTemplateEngine;
import org.simpleframework.module.resource.template.TemplateEngine;

public class ResourceManager {
   
   private final ActionAssembler assembler;
   private final SubscriptionRouter router;
   private final ComponentManager manager;
   private final ContainerServer server;
   private final ResourceSystem system;
   private final ResourceBinder binder;
   
   public ResourceManager(ServiceAssembler assembler, ComponentManager manager, ClassPath path) throws IOException {
      this.assembler = new ActionAssembler(manager, path);
      this.router = new SubscriptionRouter(manager);
      this.system = new ResourceSystem(manager, this.assembler);
      this.binder = new ResourceBinder(assembler, manager, path);
      this.server = new ContainerServer(system, router);
      this.manager = manager;
   }

   public Server create(Context context, int threads) {
      ThreadPool pool = new ThreadPool(threads);
      ContentTypeResolver resolver = new ContentTypeResolver();
      FileManager fileManager = new FileManager();
      FileResolver fileResolver = new FileResolver(fileManager);
      TemplateEngine engine = new StringTemplateEngine(fileResolver);
      
      binder.register(pool);
      binder.register(engine);
      binder.register(fileResolver);
      binder.register(manager);
      binder.register(resolver);

      return new Server() {
         
         @Override
         public Acceptor start(String name, String cookie, int threads) {
            Acceptor acceptor = server.start();
            
            binder.register(acceptor);
            binder.start(context);
            
            return acceptor;
         }
      };
   }
}
