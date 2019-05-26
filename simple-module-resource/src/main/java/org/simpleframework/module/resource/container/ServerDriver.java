package org.simpleframework.module.resource.container;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.Driver;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.ComponentStore;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ValueExtractor;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.service.ServiceAssembler;
import org.simpleframework.module.service.ServiceBinder;

public class ServerDriver implements Driver<Server> {

   private final ComponentManager manager;
   
   public ServerDriver() {
      this.manager = new ComponentStore();
   }
   
   @Override
   public Server create(ClassPath path, Context context) throws Exception {
      List<Extractor> extractors = new LinkedList<>();  
      ServiceAssembler assembler = new ServiceAssembler(manager, extractors, argument -> false);
      ServiceBinder binder = new ServiceBinder(assembler, manager, path);
      ServerBuilder builder = new ServerBuilder(binder, manager, path);
      Extractor extractor = new ValueExtractor();
      
      binder.register(path);
      binder.register(manager);
      extractors.add(extractor);
      
      return builder.create(context, 10);
   }
}
