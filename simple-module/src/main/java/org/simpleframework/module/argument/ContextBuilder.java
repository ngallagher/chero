package org.simpleframework.module.argument;

import java.util.Map;
import java.util.Set;

import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.MapContext;
import org.simpleframework.module.core.Model;

public class ContextBuilder {

   private final AttributeCombiner combiner;
   private final Context context;
   
   public ContextBuilder(String... paths) {
      this.combiner = new AttributeCombiner(paths);
      this.context = new MapContext();
   }
   
   public Context create(String[] arguments) {
      Map<String, String> map = combiner.combine(arguments);
      Set<String> names = map.keySet();
      
      for(String name : names) {
         String value = map.get(name);
         Model model = context.getModel();
         
         model.set(name, value);
      }
      return context;
   }
   
}
