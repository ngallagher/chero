package org.simpleframework.module.resource;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.simpleframework.module.ComponentManager;
import org.simpleframework.module.DependencyManager;
import org.simpleframework.module.build.ComponentBuilder;
import org.simpleframework.module.build.DependencyScanner;
import org.simpleframework.module.build.extract.Extractor;
import org.simpleframework.module.build.extract.ValueExtractor;
import org.simpleframework.module.common.ComponentListener;
import org.simpleframework.module.common.DependencyTreeScanner;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.MapContext;

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
