package org.simpleframework.module.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.simpleframework.module.Application;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.ComponentStore;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ValueExtractor;
import org.simpleframework.module.graph.ClassPath;

public class ServiceApplication implements Application<Service> {

   private final ComponentManager manager;
   
   public ServiceApplication() {
      this.manager = new ComponentStore();
   }
   
   @Override
   public Service create(ClassPath path, Context context) {
      Set<Class> ignore = new HashSet<>();
      List<Extractor> extractors = new LinkedList<>();
      ServiceAssembler assembler = new ServiceAssembler(manager, extractors, argument -> false);
      Extractor extractor = new ValueExtractor();
      
      manager.register(path);
      manager.register(manager);
      extractors.add(extractor);
      
      return () -> assembler.assemble(path, context, ignore);
   }
}
