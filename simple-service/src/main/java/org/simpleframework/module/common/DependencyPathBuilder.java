package org.simpleframework.module.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Module;

import io.github.classgraph.ClassInfo;

public class DependencyPathBuilder {

   private final DependencyGraphBuilder builder;

   public DependencyPathBuilder(Class<?>... modules) {
      this.builder = new DependencyGraphBuilder(modules);
   }

   public DependencyPath create() {
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

   private static class ImportPath implements DependencyPath {
      
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
