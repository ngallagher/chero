package org.simpleframework.module.graph;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Module;

import io.github.classgraph.ClassInfo;

public class ClassPathBuilder {

   private final DependencyGraphBuilder builder;

   public ClassPathBuilder(Set<Class> modules) {
      this.builder = new DependencyGraphBuilder(modules);
   }

   public ClassPath create() {
      String module = Module.class.getName();
      String component = Component.class.getName();
      DependencyGraph graph = builder.create();
      Predicate<String> predicate = graph.getPredicate();
      Iterator<ClassInfo> iterator = graph.getGraph()
            .scan()
            .getAllClasses()
            .iterator();

      if (iterator.hasNext()) {
         ImportPath path = new ImportPath(predicate);
         
         while (iterator.hasNext()) {
            ClassInfo next = iterator.next();
            String name = next.getName();
            
            if (next.hasAnnotation(component)) {
               path.components.put(name, next);
            } else if(next.hasAnnotation(module)) {
               path.modules.put(name, next);
            } 
            path.objects.put(name, next);
         }
         return path;
      }
      return new ImportPath(name -> false);
   }

   private static class ImportPath implements ClassPath {
      
      private final Map<String, ClassInfo> components;
      private final Map<String, ClassInfo> modules;
      private final Map<String, ClassInfo> objects;
      private final Predicate<String> predicate;
      
      public ImportPath(Predicate<String> predicate) {
         this.components = new HashMap<>();
         this.modules = new HashMap<>();
         this.objects = new HashMap<>();
         this.predicate = predicate;
      }
      
      @Override
      public Set<Class> getTypes(Class<? extends Annotation> type) {
         String name = type.getName();
         return getObjects()
               .values()
               .stream()
               .filter(info -> {
                  String qualifier = info.getName();
               
                  if(predicate.test(qualifier)) {
                     return info.hasAnnotation(name);
                  }
                  return false;
               })
               .map(ClassInfo::loadClass)
               .collect(Collectors.toSet());
      }

      @Override
      public Map<String, ClassInfo> getModules() {
         return modules;
      }

      @Override
      public Map<String, ClassInfo> getComponents() {
         return components;
      }

      @Override
      public Map<String, ClassInfo> getObjects() {
         return objects;
      }

      @Override
      public Predicate<String> getPredicate() {
         return predicate;
      }
   }
}
