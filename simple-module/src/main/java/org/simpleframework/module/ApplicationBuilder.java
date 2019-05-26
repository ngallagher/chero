package org.simpleframework.module;

import java.util.LinkedHashSet;
import java.util.Set;

public class ApplicationBuilder<T> {
   
   public static <T> ApplicationBuilder<T> create(Class<? extends Application<T>> type) {
      return new ApplicationBuilder<T>(type);
   }
   
   private final ApplicationLauncher<T> launcher;
   private final Set<Class> modules;
   private final Set<String> paths;
   
   private ApplicationBuilder(Class<? extends Application<T>> type) {
      this.modules = new LinkedHashSet<>();
      this.paths = new LinkedHashSet<>();
      this.launcher = new ApplicationLauncher<T>(type, modules, paths);
   }
   
   public ApplicationBuilder<T> withModule(Class module) {
      modules.add(module);
      return this;
   }
   
   public ApplicationBuilder<T> withPath(String path) {
      paths.add(path);
      return this;
   }
   
   public T withArguments(String[] arguments) {
      if(paths.isEmpty()) {
         throw new IllegalArgumentException("Application requires a configuration path");
      }
      if(modules.isEmpty()) {
         throw new IllegalArgumentException("Application requires a module");
      }
      return launcher.create(arguments);
   }
}
