package org.simpleframework.module.build;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

import org.simpleframework.module.core.ComponentListener;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.graph.ClassPath;
import org.simpleframework.module.graph.DependencyTree;
import org.simpleframework.module.graph.DependencyTreeScanner;

public class ServiceAssembler {
   
   private final DependencyTreeScanner calculator;
   private final ConstructorScanner scanner;
   private final ComponentManager manager;
   
   public ServiceAssembler(ComponentManager manager, List<Extractor> extractors, Predicate<Argument> filter) {
      this.scanner = new ConstructorScanner(manager, extractors, filter);
      this.calculator = new DependencyTreeScanner();
      this.manager = manager;
   }

   public Service assemble(ClassPath path, Context context, Set<Class> ignore) {
      try {
         DependencyTree tree = calculator.scan(path, ignore);
         Queue<Class> queue = tree.getOrder();
         
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
         return () -> manager.resolveAll(ComponentListener.class).forEach(ComponentListener::onReady);
      }catch(Exception e) {
         throw new IllegalStateException("Could not start application", e);
      }
   }
}
