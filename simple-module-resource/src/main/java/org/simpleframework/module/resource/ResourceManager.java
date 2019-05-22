package org.simpleframework.module.resource;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.socket.service.Service;
import org.simpleframework.module.DependencyManager;
import org.simpleframework.module.common.DependencyTree;
import org.simpleframework.module.common.ThreadPool;
import org.simpleframework.module.resource.action.ActionAssembler;
import org.simpleframework.module.resource.action.ActionMatcher;
import org.simpleframework.module.resource.container.ResourceServer;
import org.simpleframework.module.resource.container.ResourceServerBuilder;
import org.simpleframework.module.resource.template.StringTemplateEngine;
import org.simpleframework.module.resource.template.TemplateEngine;

public class ResourceManager {
   
   public static List<Class> getResources() {
      return Arrays.asList(ContentTypeResolver.class,
            SubscriptionRouter.class,
            ResourceSystem.class,
            ResourceServerBuilder.class,
            ResourceServer.class,
            FileManager.class,
            FileResolver.class,
            TemplateEngine.class,
            ThreadPool.class);
   }

   public static void register(DependencyManager context, DependencyTree tree, int port) {
      context.register(context);
      
      ActionMatcher matcher = ActionAssembler.assemble(context, tree);
      List<ResourceMatcher> matchers = context.resolveAll(ResourceMatcher.class);
      List<Service> services = context.resolveAll(Service.class);
      
      // this is totally crap
      ThreadPool pool = new ThreadPool(10);
      ContentTypeResolver resolver = new ContentTypeResolver();
      SubscriptionRouter router = new SubscriptionRouter(services);
      ResourceSystem system = new ResourceSystem(matcher, matchers);
      ResourceServerBuilder serverBuilder = new ResourceServerBuilder(system, router);
      ResourceServer server = new ResourceServer(serverBuilder, port);
      FileManager manager = new FileManager();
      FileResolver fileResolver = new FileResolver(manager);
      TemplateEngine engine = new StringTemplateEngine(fileResolver);

      context.register(pool);
      context.register(engine);
      context.register(fileResolver);
      context.register(manager);
      context.register(resolver);
      context.register(matcher);
      context.register(system);
      context.register(server);
   }
}
