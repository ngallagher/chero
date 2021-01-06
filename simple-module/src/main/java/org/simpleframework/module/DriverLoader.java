package org.simpleframework.module;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Set;

import org.simpleframework.module.argument.ContextBuilder;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.index.ClassPathBuilder;
import org.simpleframework.module.path.ClassPath;

public class DriverLoader<T> {
   
   private final Class<? extends Driver<T>> type;
   private final ClassPathBuilder builder;
   private final ContextBuilder loader;
   private final Set<String> sources;

   public DriverLoader(Class<? extends Driver<T>> type, Set<Class> modules, Set<String> sources) {
      this(type,  modules, sources, Collections.EMPTY_SET);
   }
   
   public DriverLoader(Class<? extends Driver<T>> type, Set<Class> modules, Set<String> sources, Set<String> paths) {
      this(type, modules, sources, paths, null);
   }

   public DriverLoader(Class<? extends Driver<T>> type, Set<Class> modules, Set<String> sources, Set<String> paths, String extension) {
      this.loader = new ContextBuilder(paths, extension);
      this.builder = new ClassPathBuilder(modules);
      this.sources = sources;
      this.type = type;
   }
   
   public T create(String... arguments) {
      try {
         Context context = loader.read(sources, arguments);
         ClassPath path = builder.create();
         Constructor<? extends Driver<T>> constructor = type.getDeclaredConstructor();

         constructor.setAccessible(true);

         return constructor.newInstance().create(path, context);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create application " + type, e);
      }
   }
}
