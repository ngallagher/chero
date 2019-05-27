package org.simpleframework.module.argument;

import java.util.Map;
import java.util.Set;

import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Interpolator;
import org.simpleframework.module.core.MapContext;
import org.simpleframework.module.core.Model;

public class ContextBuilder {

   private final AttributeCombiner combiner;
   private final Interpolator interpolator;
   private final Context context;
   
   public ContextBuilder(Set<String> files, Set<String> paths) {
      this.combiner = new AttributeCombiner(files, paths);
      this.context = new MapContext();
      this.interpolator = new Interpolator(context);
   }
   
   public Context create(String... arguments) {
      Map<String, String> map = combiner.combine(arguments);
      Set<String> names = map.keySet();
      Model model = context.getModel();
      
      for(String name : names) {
         String value = map.get(name);
         String update = interpolator.interpolate(value);
         
         model.set(name, update);
      }
      for(String name : names) {
         String value = model.get(name);
         String update = interpolator.interpolate(value);
         
         System.setProperty(name, update);
         model.set(name, update);         
      }
      return context;
   }
   
}
