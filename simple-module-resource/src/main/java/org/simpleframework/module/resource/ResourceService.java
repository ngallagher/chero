package org.simpleframework.module.resource;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.simpleframework.module.build.ComponentBuilder;
import org.simpleframework.module.build.DependencyScanner;
import org.simpleframework.module.build.extract.Extractor;
import org.simpleframework.module.build.extract.ValueExtractor;
import org.simpleframework.module.common.ComponentListener;
import org.simpleframework.module.common.ComponentManager;
import org.simpleframework.module.common.DependencyManager;
import org.simpleframework.module.common.DependencyPath;
import org.simpleframework.module.common.DependencyTree;
import org.simpleframework.module.common.DependencyTreeScanner;
import org.simpleframework.module.context.Context;

public class ResourceService {
   
   private final DependencyManager manager;
   private final DependencyPath path;
   
   public ResourceService(DependencyPath path) {
      this.manager = new ComponentManager();
      this.path = path;
   }
   
   public void start(Context context, int port) throws Exception {
      List<Extractor> extractors = new LinkedList<>();
      DependencyScanner scanner = new DependencyScanner(manager, extractors);
      Extractor extractor = new ValueExtractor();
      ResourceManager resourceManager = new ResourceManager(manager, path);
      DependencyTreeScanner dependencyScanner = new DependencyTreeScanner(path);   
      
      Class[] types = resourceManager.create(port);   
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
      manager.resolveAll(ComponentListener.class)
      .forEach(ComponentListener::onReady);
   }
}
