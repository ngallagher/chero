package org.simpleframework.module;

import java.util.LinkedHashSet;
import java.util.Set;

public class Application {
   
   public static <T> Binder<T> create(Class<? extends Driver<T>> type) {
      return new ApplicationBinder<T>(type);
   }
   
   private static class ApplicationBinder<T> implements Binder<T> {
      
      private final DriverLoader<T> launcher;
      private final Set<Class> modules;
      private final Set<String> files;
      private final Set<String> paths;
      
      private ApplicationBinder(Class<? extends Driver<T>> type) {
         this.modules = new LinkedHashSet<>();
         this.paths = new LinkedHashSet<>();
         this.files = new LinkedHashSet<>();
         this.launcher = new DriverLoader<T>(type, modules, files, paths);
      }

      @Override
      public Binder<T> register(Class... modules) {
         for(Class module : modules) {
            register(module);
         }
         return this;
      }

      @Override
      public Binder<T> register(Class module) {
         if(module != null) {
            modules.add(module);
         }
         return this;
      }
      
      @Override
      public Binder<T> path(String path) {
         if(path != null) {
            paths.add(path);
         }
         return this;
      }
      
      @Override
      public Binder<T> file(String file) {
         if(file != null) {
            files.add(file);
         }
         return this;
      }
      
      @Override
      public T create(String... arguments) {
         if(modules.isEmpty()) {
            throw new IllegalArgumentException("Driver requires at least one module");
         }
         return launcher.create(arguments);
      }
   }
}
