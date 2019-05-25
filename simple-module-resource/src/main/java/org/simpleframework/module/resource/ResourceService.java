package org.simpleframework.module.resource;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.simpleframework.module.build.ConstructorScanner;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.core.ComponentListener;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.ComponentStore;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ValueExtractor;
import org.simpleframework.module.graph.DependencyPath;
import org.simpleframework.module.graph.DependencyTree;
import org.simpleframework.module.graph.DependencyTreeScanner;
import org.simpleframework.module.resource.action.build.ComponentFilter;

public class ResourceService {
   
   private final ComponentManager manager;
   private final DependencyPath path;
   
   public ResourceService(DependencyPath path) {
      this.manager = new ComponentStore();
      this.path = path;
   }
   
   public void start(Context context, int port) throws Exception {
      List<Extractor> extractors = new LinkedList<>();
      ComponentFilter filter = new ComponentFilter();
      ConstructorScanner scanner = new ConstructorScanner(manager, extractors, filter);
      Extractor extractor = new ValueExtractor();
      ResourceManager resourceManager = new ResourceManager(manager, path);
      DependencyTreeScanner dependencyScanner = new DependencyTreeScanner(path);   
      
      Class[] types = resourceManager.create(scanner, port);   
      DependencyTree tree = dependencyScanner.scan(types);
      Queue<Class> queue = tree.getOrder();
      
      extractors.add(extractor);
      
      while(!queue.isEmpty()) {
         Class type = queue.poll();
         List<Function> builders = scanner.createConstructors(type);
         Iterator<Function> iterator = builders.iterator();
         
         while(iterator.hasNext()) {
            try {
               Function builder = iterator.next();
               Object instance = builder.getValue(context);
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
