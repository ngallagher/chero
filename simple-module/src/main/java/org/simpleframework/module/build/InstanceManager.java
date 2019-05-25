package org.simpleframework.module.build;

import java.util.function.Predicate;

import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;

public class InstanceManager {

   private final Predicate<Argument> transients;
   private final ComponentManager manager;
   
   public InstanceManager(ComponentManager manager, Predicate<Argument> transients) {
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
