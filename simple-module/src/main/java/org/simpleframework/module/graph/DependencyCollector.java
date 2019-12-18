package org.simpleframework.module.graph;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.simpleframework.module.annotation.Import;
import org.simpleframework.module.index.ModuleFilter;
import org.simpleframework.module.index.ProviderCollector;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.ConstructorNode;
import org.simpleframework.module.path.MethodNode;
import org.simpleframework.module.path.ParameterNode;

class DependencyCollector {

   private final Map<String, Set<Dependency>> index;
   private final DependencyResolver resolver;
   private final ProviderCollector collector;
   private final ModuleFilter filter;
   private final ClassPath path;
   
   public DependencyCollector(ModuleFilter filter, ClassPath path) {
      this.resolver = new DependencyResolver(filter, path);
      this.collector = new ProviderCollector();
      this.index = new HashMap<>(); 
      this.filter = filter;
      this.path = path;      
   }
   
   public Set<Dependency> collect(String name) {
      return index.computeIfAbsent(name, this::resolve);     
   }
   
   private Set<Dependency> resolve(String name) {
      Set<Dependency> arguments = resolveArguments(name);
      Set<Dependency> depends = resolveDepends(name);
      
      return Arrays.asList(arguments, depends)
            .stream()
            .flatMap(Set<Dependency>::stream)
            .collect(Collectors.toSet());
   }
   
   private Set<Dependency> resolveArguments(String name) {      
      ClassNode node = path.getType(name);
      
      if(filter.isComponent(node)) {
         return node.getConstructors()
               .stream()
               .map(this::resolveConstructor)
               .flatMap(Set<Dependency>::stream)
               .collect(Collectors.toSet());   
      }
      if(filter.isProvided(node)) { // does a module provide this node
         return collector.collect(filter, path, node)
               .stream()
               .map(this::resolveProvider)
               .flatMap(Set<Dependency>::stream)
               .collect(Collectors.toSet());   
      }
      return Collections.emptySet();            
   }

   private Set<Dependency> resolveDepends(String name) {
      ClassNode node = path.getType(name);
      
      if(filter.isDependent(node)) {
         Import required = node.getAnnotation(Import.class);
         
         if(required != null) {
            Class[] types = required.value();
            
            return Arrays.asList(types)
               .stream()
               .map(Class::getName)
               .map(path::getType)
               .filter(filter::isVisible)
               .map(resolver::resolve)
               .collect(Collectors.toSet());
                  
         }
      }
      return Collections.emptySet();
   }
   
   private Set<Dependency> resolveConstructor(ConstructorNode constructor) {
      return constructor.getParameters()
            .stream() 
            .map(ParameterNode::getType)
            .filter(Objects::nonNull) 
            .map(resolver::resolve)
            .collect(Collectors.toSet());
   }
   
   private Set<Dependency> resolveProvider(MethodNode method) {
      return method.getParameters()
            .stream() 
            .map(ParameterNode::getType)  
            .filter(Objects::nonNull) 
            .map(resolver::resolve)
            .collect(Collectors.toSet());
   }
}
