package org.simpleframework.module.graph;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.build.ConstructorScanner;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.build.MethodScanner;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.index.ModuleFilter;
import org.simpleframework.module.index.ProviderCollector;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.MethodNode;

public class DependencyProvider {
   
   private final ConstructorScanner constructors;
   private final ProviderCollector collector;
   private final MethodScanner methods;

   public DependencyProvider(ComponentManager manager, List<Extractor> extractors, Predicate<Argument> filter) {
      this.constructors = new ConstructorScanner(manager, extractors, filter);
      this.methods = new MethodScanner(manager, constructors, extractors, filter);
      this.collector = new ProviderCollector();
   }
   
   public List<Function> createProviders(ModuleFilter filter, ClassPath path, Class type) throws Exception {
      String name = type.getName();
      ClassNode node = path.getType(name);
      
      if(filter.isProvided(node)) {
         return collector.collect(filter, path, node)
            .stream()   
            .map(MethodNode::getDeclaringClass)
            .map(ClassNode::getType)
            .flatMap(parent -> {
               try {
                  return methods.createMethods(parent)
                     .stream()
                     .filter(function -> 
                        function.isAnnotationPresent(Provides.class) && 
                        function.getReturnType().equals(type));
               } catch(Exception e) {
                  throw new IllegalStateException("Could not create " + type + " from " + parent, e);
               }
            })
            .collect(Collectors.toList());
      }
      return constructors.createConstructors(type);
   }
}
