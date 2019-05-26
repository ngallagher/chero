package org.simpleframework.module;

import java.lang.reflect.Constructor;
import java.util.Set;

import org.simpleframework.module.argument.ContextBuilder;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.graph.ClassPath;
import org.simpleframework.module.graph.ClassPathBuilder;

public class ApplicationLauncher<T> {
   
   private final Class<? extends Application<T>> type;
   private final ClassPathBuilder builder;
   private final ContextBuilder loader;
   
   public ApplicationLauncher(Class<? extends Application<T>> type, Set<Class> modules, Set<String> paths) {
      this.loader = new ContextBuilder(paths);
      this.builder = new ClassPathBuilder(modules);
      this.type = type;
   }
   
   public T create(String[] arguments) {
      try {
         ClassPath path = builder.create();
         Context context = loader.create(arguments);
         Constructor<? extends Application<T>> constructor = type.getDeclaredConstructor();
         
         if(!constructor.isAccessible()) {
            constructor.setAccessible(true);
         }
         return constructor.newInstance().create(path, context);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create application " + type, e);
      }
   }
}
