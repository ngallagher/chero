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
import org.simpleframework.module.common.DependencyPath;
import org.simpleframework.module.common.DependencyPathBuilder;
import org.simpleframework.module.common.DependencyTree;
import org.simpleframework.module.common.DependencyTreeScanner;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.MapContext;

public class Application {

   public static void start(int port, Class<?>... modules) throws Exception {
      List<Extractor> extractors = new LinkedList<>();
      DependencyManager manager = new ComponentManager();
      DependencyScanner scanner = new DependencyScanner(manager, extractors);
      Context context = new MapContext();
      Extractor extractor = new ValueExtractor();
      DependencyPathBuilder dependencyBuilder = new DependencyPathBuilder(modules);
      DependencyPath path = dependencyBuilder.create();
      ResourceManager resourceManager = new ResourceManager(manager, path, port);
      Class[] types = resourceManager.create();
      DependencyTreeScanner dependencyScanner = new DependencyTreeScanner(path);      
      DependencyTree tree = dependencyScanner.scan(types);
      Queue<Class> queue = tree.getOrder();
      
      extractors.add(extractor);
      
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
      manager.resolveAll(ComponentListener.class).forEach(ComponentListener::onReady);
   }
}
