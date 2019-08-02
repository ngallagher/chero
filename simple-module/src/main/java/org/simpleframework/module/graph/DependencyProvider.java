package org.simpleframework.module.graph;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.build.ConstructorScanner;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.build.MethodScanner;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.index.ModuleFilter;
import org.simpleframework.module.index.ProviderChecker;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.MethodNode;

public class DependencyProvider {
   
   private final ConstructorScanner constructors;
   private final MethodScanner methods;
   private final ProviderChecker checker;

   public DependencyProvider(ComponentManager manager, List<Extractor> extractors, Predicate<Argument> filter) {
      this.constructors = new ConstructorScanner(manager, extractors, filter);
      this.methods = new MethodScanner(manager, constructors, extractors, filter);
      this.checker = new ProviderChecker();
   }
   
   public List<Function> createProviders(ModuleFilter filter, ClassPath path, Class type) throws Exception {
      String name = type.getName();
      ClassNode node = path.getType(name);
      
      if(filter.isProvided(node)) {
         Class<?> parent = path.getTypes(Module.class)
               .stream()
               .filter(filter::isVisible)
               .map(ClassNode::getMethods)
               .flatMap(Collection<MethodNode>::stream)
               .filter(method -> checker.isProvider(method, node))
               .findFirst()
               .map(MethodNode::getDeclaringClass)
               .get()
               .getType();
         
         if(parent != null) {
            return methods.createMethods(parent)
                  .stream()
                  .filter(function -> 
                     function.isAnnotationPresent(Provides.class) &&
                     function.getReturnType().equals(type))
                  .collect(Collectors.toList());
         }
      }
      return constructors.createConstructors(type);
   }
}
