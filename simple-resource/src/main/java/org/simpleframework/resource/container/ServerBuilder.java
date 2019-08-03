package org.simpleframework.resource.container;

import org.simpleframework.module.common.ThreadPool;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.service.ServiceBinder;
import org.simpleframework.resource.FileManager;
import org.simpleframework.resource.FileResolver;
import org.simpleframework.resource.MediaTypeResolver;
import org.simpleframework.resource.ResourceManager;
import org.simpleframework.resource.SubscriptionRouter;
import org.simpleframework.resource.action.Schema;
import org.simpleframework.resource.template.StringTemplateEngine;
import org.simpleframework.resource.template.TemplateEngine;

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
      MediaTypeResolver resolver = new MediaTypeResolver();
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
