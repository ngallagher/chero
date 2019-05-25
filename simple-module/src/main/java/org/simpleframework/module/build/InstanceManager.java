package org.simpleframework.module.build;

import java.util.function.Predicate;

import org.simpleframework.module.common.DependencyManager;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.Model;

public class InstanceManager {

   private final Predicate<Argument> transients;
   private final DependencyManager manager;
   
   public InstanceManager(DependencyManager manager, Predicate<Argument> transients) {
      this.transients = transients;
      this.manager = manager;
   }
   
   public Object get(Argument argument, Context context) {
      Model model = context.getModel();
      Class type = argument.getType();
      
      if(transients.test(argument)) {
         return model.get(type);
      }
      return manager.resolve(type);
   }
   
   public void set(Argument argument, Context context, Object value) {
      Model model = context.getModel();
      Class type = argument.getType();
      
      if(transients.test(argument)) {
         model.set(type, value);
      } else {
         manager.register(value);
      }
   }
}
