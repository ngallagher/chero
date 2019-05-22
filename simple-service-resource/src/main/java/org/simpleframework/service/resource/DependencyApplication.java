package org.simpleframework.service.resource;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.simpleframework.service.ComponentManager;
import org.simpleframework.service.DependencyManager;
import org.simpleframework.service.build.ComponentBuilder;
import org.simpleframework.service.build.DependencyScanner;
import org.simpleframework.service.build.extract.Extractor;
import org.simpleframework.service.build.extract.ValueExtractor;
import org.simpleframework.service.common.ComponentListener;
import org.simpleframework.service.common.DependencyTreeScanner;
import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.MapContext;

public class DependencyApplication {

   public static DependencyManager start(int port, Class<?>... modules) throws Exception {
      List<Extractor> extractors = new LinkedList<>();
      DependencyTreeScanner dependencyScanner = new DependencyTreeScanner(modules);
      DependencyManager manager = new ComponentManager();
      DependencyScanner scanner = new DependencyScanner(manager, extractors);
      Context context = new MapContext();
      Extractor extractor = new ValueExtractor();
      
      Class<?>[] types = ResourceManager.getResources().stream().toArray(Class[]::new);
      Queue<Class> queue = dependencyScanner.scan(types);
      
      extractors.add(extractor);
      ResourceManager.register(manager, port);
      
      while(!queue.isEmpty()) {
         Class type = queue.poll();
         List<ComponentBuilder> builders = scanner.createBuilders(type);
         Iterator<ComponentBuilder> iterator = builders.iterator();
         
         while(iterator.hasNext()) {
            try {
               ComponentBuilder builder = iterator.next();
               builder.build(context);
               break;
            } catch(Exception e) {
               e.printStackTrace();
            }
         }
      }
      manager.resolveAll(ComponentListener.class)
         .forEach(listener -> listener.onReady());
      return manager;
   }
}
