package org.simpleframework.module.resource.container;

import java.io.IOException;

import org.simpleframework.module.common.ThreadPool;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.resource.ContentTypeResolver;
import org.simpleframework.module.resource.FileManager;
import org.simpleframework.module.resource.FileResolver;
import org.simpleframework.module.resource.ResourceManager;
import org.simpleframework.module.resource.SubscriptionRouter;
import org.simpleframework.module.resource.action.Schema;
import org.simpleframework.module.resource.template.StringTemplateEngine;
import org.simpleframework.module.resource.template.TemplateEngine;
import org.simpleframework.module.service.ServiceBinder;

class ServerBuilder {
   
   private final ResourceManager assembler;
   private final SubscriptionRouter router;
   private final ContainerServer server;
   private final ServiceBinder binder;
   
   public ServerBuilder(ServiceBinder binder, ComponentManager manager, ClassPath path, Schema schema) {
      this.assembler = new ResourceManager(manager, path, schema);
      this.router = new SubscriptionRouter(manager);
      this.server = new ContainerServer(assembler, router);
      this.binder = binder;
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
      binder.register(resolver);

      return new ServerBinder() {
         
         @Override
         public Server register(Object instance) {
            binder.register(instance);
            return this;
         }
         
         @Override
         public Acceptor start(String name, String cookie, int threads) {
            Acceptor acceptor = server.start(name, cookie, threads);
            
            binder.register(acceptor);
            binder.start(context);
            
            return acceptor;
         }
      };
   }
}
