package org.simpleframework.module.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.simpleframework.module.annotation.Import;
import org.simpleframework.module.annotation.Module;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassInfo;

class PackageList {

   private final Function<String, Class> loader;
   private final Map<String, ClassInfo> index;
   private final Set<Class> types;

   public PackageList(ClassFinder finder, Set<Class> types) {
      this.index = finder.findAll();
      this.types = types;
      this.loader = name -> {
         try {
            return Class.forName(name);
         } catch(Exception e) {
            throw new IllegalStateException("Could not load " + name, e);
         }
      };
   }

   public Set<String> list() {
      Set<ClassInfo> modules = new HashSet<>();
      Set<Class<?>> imports = new HashSet<>();

      if(!index.isEmpty()) {
         String required = Module.class.getName();
         Set<String> names = index.keySet();

         for(String next : names) {
            ClassInfo info = index.get(next);
            AnnotationInfo annotation = info.getAnnotationInfo(required);

            if(annotation != null) {
               modules.add(info);
            }
         }
      }
      if(!types.isEmpty()) {
         for(Class<?> type : types) {
            String name = type.getName();
            ClassInfo node = index.get(name);

            if(node == null) {
               throw new IllegalArgumentException("Could not resolve " + type);
            }
            collect(type, modules, imports);
         }
      }
      return reduce(imports);
   }

   private Set<String> reduce(Set<Class<?>> imports) {
      Set<String> packages = new HashSet<>();
      List<String> sorted = new ArrayList<>();

      for(Class<?> imported : imports) {
         Package module = imported.getPackage();
         String name = module.getName();

         sorted.add(name);
      }
      Collections.sort(sorted, (a, b) -> a.length() - b.length());

      for(String name : sorted) {
         if(packages.add(name)) {
            String prefix = name;

            while(prefix.contains(".")) {
               int index = prefix.lastIndexOf(".");

               if(index != -1) {
                  prefix = prefix.substring(0, index);

                  if(packages.contains(prefix)) {
                     packages.remove(name); // a child is already there
                  }
               }
            }
         }
      }
      return Collections.unmodifiableSet(packages);
   }

   private void collect(Class<?> type, Set<ClassInfo> modules, Set<Class<?>> done) {
      String name = type.getName();
      ClassInfo node = index.get(name);

      if(node == null) {
         throw new IllegalArgumentException("Could not resolve " + type);
      }
      Module module = type.getAnnotation(Module.class);

      if(module == null) {
         throw new IllegalStateException("Import " + type + " is not a module ");
      }
      collect(node, modules, done);
   }

   private void collect(ClassInfo node, Set<ClassInfo> modules, Set<Class<?>> done) {
      String type = node.getName();
      Class<?> actual = loader.apply(type);

      if(done.add(actual)) {
         Import imports = actual.getAnnotation(Import.class);
         String prefix = actual.getPackage().getName();

         if(imports != null) {
            Class<?>[] dependencies = imports.value();

            if(dependencies.length == 0) {
               throw new IllegalStateException("Import on " + type + " does not import any modules");
            }
            for(Class<?> dependency : dependencies) {
               collect(dependency, modules, done);
            }
         }
         for(ClassInfo info : modules) {
            String name = info.getName();

            if(name.startsWith(prefix) && !name.equals(type)) {
               collect(info, modules, done);
            }
         }
      }
   }
}
