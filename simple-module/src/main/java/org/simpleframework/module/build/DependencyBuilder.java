package org.simpleframework.module.build;

import java.lang.reflect.Constructor;

import org.simpleframework.module.common.DependencyManager;
import org.simpleframework.module.context.Context;

public class DependencyBuilder implements ComponentBuilder {

   private final DependencyManager manager;
   private final ParameterBuilder builder;
   private final PropertyInjector injector;
   private final Constructor factory;

   public DependencyBuilder(DependencyManager manager, ParameterBuilder builder, PropertyInjector injector, Constructor factory) {
      this.builder = builder;
      this.injector = injector;
      this.factory = factory;
      this.manager = manager;
   }

   @Override
   public Class[] require() throws Exception {
      return builder.require();
   }

   @Override
   public <T> T build(Context context) throws Exception {
      Object instance = resolve(context);

      if (injector != null) {
         injector.inject(instance, context);
      }
      return (T)instance;
   }
   
   private <T> T resolve(Context context) throws Exception {
      Class type = factory.getDeclaringClass();
      Object instance = manager.resolve(type);
      
      if(instance == null) {
         Object[] arguments = builder.extract(context);
         Object object = factory.newInstance(arguments);
   
         if (object != null) {
            manager.register(object);
         }
         return (T)object;
      }
      return (T)instance;
   }

   @Override
   public float score(Context context) throws Exception {
      if (injector.valid(context)) {
         return builder.score(context);
      }
      return -1;
   }
}
