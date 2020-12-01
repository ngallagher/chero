package org.simpleframework.module.argument;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Interpolator;
import org.simpleframework.module.core.MapContext;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.core.TokenFilter;
import org.simpleframework.module.core.ContextFilter;

public class ContextBuilder {

   private final AttributeCombiner combiner;
   private final Interpolator interpolator;
   private final TokenFilter filter;
   private final Context context;

   public ContextBuilder() {
      this(Collections.EMPTY_SET);
   }

   public ContextBuilder(Set<String> paths) {
      this.combiner = new AttributeCombiner(paths);
      this.context = new MapContext();
      this.filter = new ContextFilter(context);
      this.interpolator = new Interpolator(filter);
   }
   
   public Context read(Iterable<String> files, String... arguments) {
      Map<String, String> map = combiner.combine(files, arguments);
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
