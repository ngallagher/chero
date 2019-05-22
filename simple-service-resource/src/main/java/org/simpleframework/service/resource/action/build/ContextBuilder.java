package org.simpleframework.service.resource.action.build;

import java.lang.reflect.Constructor;

import org.simpleframework.service.build.ComponentBuilder;
import org.simpleframework.service.build.PropertyInjector;
import org.simpleframework.service.build.extract.ParameterBuilder;
import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.Model;

public class ContextBuilder implements ComponentBuilder {

   private final PropertyInjector injector;
   private final ParameterBuilder builder;
   private final Constructor factory;

   public ContextBuilder(ParameterBuilder builder, PropertyInjector injector, Constructor factory) {
      this.builder = builder;
      this.injector = injector;
      this.factory = factory;
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
      Model model = context.getModel();
      Object instance = model.get(type);
      
      if(instance == null) {
         Object[] arguments = builder.extract(context);
         Object object = factory.newInstance(arguments);
   
         if (object != null) {
            model.set(type, object);
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

