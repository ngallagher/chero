package org.simpleframework.module.resource;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.socket.service.Service;
import org.simpleframework.module.DependencyManager;
import org.simpleframework.module.common.DependencyPath;
import org.simpleframework.module.common.ThreadPool;
import org.simpleframework.module.resource.action.ActionAssembler;
import org.simpleframework.module.resource.action.ActionMatcher;
import org.simpleframework.module.resource.container.ResourceServer;
import org.simpleframework.module.resource.container.ResourceServerBuilder;
import org.simpleframework.module.resource.template.StringTemplateEngine;
import org.simpleframework.module.resource.template.TemplateEngine;

public class ResourceManager {
   
   private final ActionAssembler assembler;
   private final DependencyManager context;
   private final int port;
   
   public ResourceManager(DependencyManager context, DependencyPath path, int port) {
      this.assembler = new ActionAssembler(context, path);
      this.context = context;
      this.port = port;
   }

   public Class[] create() {
      context.register(context);
      
      ActionMatcher matcher = assembler.assemble();
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
      
      return Arrays.asList(pool, engine, fileResolver, manager, resolver, matcher, system, server)
            .stream()
            .map(object -> {
               Class<?> type = object.getClass();
               context.register(object);
               return type;
            })
            .toArray(Class[]::new);
   }
}
