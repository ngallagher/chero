package org.simpleframework.module.resource.container;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.Application;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.ComponentStore;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ValueExtractor;
import org.simpleframework.module.graph.ClassPath;
import org.simpleframework.module.service.ServiceAssembler;

public class ServerApplication implements Application<Server> {

   private final ComponentManager manager;
   
   public ServerApplication() {
      this.manager = new ComponentStore();
   }
   
   @Override
   public Server create(ClassPath path, Context context) throws Exception {
      List<Extractor> extractors = new LinkedList<>();
      ServiceAssembler assembler = new ServiceAssembler(manager, extractors, argument -> false);
      ServerBuilder builder = new ServerBuilder(assembler, manager, path);
      Extractor extractor = new ValueExtractor();
      
      manager.register(path);
      manager.register(manager);
      extractors.add(extractor);
      
      return builder.create(context, 10);
   }
}
