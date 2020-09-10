package org.simpleframework.resource.build;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.MapContext;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.core.Phase;

public class PathContextBuilder {

   private final PathResolver resolver;
   private final MethodMatcher matcher;

   public PathContextBuilder(MethodMatcher matcher) {
      this.resolver = new PathResolver();
      this.matcher = matcher;
   }

   public Context create(Context context, Phase phase) throws Exception {
      Model model = context.getModel();
      Request request = model.get(Request.class);
      Response response = model.get(Response.class);

      if(request == null || response == null) {
         throw new IllegalStateException("Could not get request or response from model");
      }
      String normalized = resolver.resolve(context);
      Map<Object, Object> attributes = (Map)matcher.evaluate(normalized);

      if(phase.isExecute()) {
         if (!attributes.isEmpty()) {
            attributes.forEach(model::set);
         }
         return context;
      }
      return new ScoreContext(model, attributes);
   }

   private static class ScoreContext extends MapContext {

      private final Model model;

      public ScoreContext(Model model, Map<Object, Object> parameters) {
         this.model = new ScoreModel(model, parameters);
      }

      @Override
      public Model getModel() {
         return model;
      }
   }

   private static class ScoreModel implements Model {

      private final Map<Object, Object> attributes;
      private final Model model;

      public ScoreModel(Model model, Map<Object, Object> attributes) {
         this.attributes = attributes;
         this.model = model;
      }

      @Override
      public Iterator<Object> iterator() {
         Set<Object> keys = new HashSet<>();

         model.iterator().forEachRemaining(keys::add);
         attributes.keySet().forEach(keys::add);

         return keys.iterator();
      }

      @Override
      public <T> T get(Object key) {
         T value = (T)attributes.get(key);

         if(value == null) {
            return model.get(key);
         }
         return value;
      }

      @Override
      public <T> T remove(Object key) {
         return (T)attributes.remove(key);
      }

      @Override
      public void set(Object key, Object object) {
         attributes.put(key, object);
      }

      @Override
      public boolean contains(Object key) {
         return attributes.containsKey(key) || model.contains(key);
      }

      @Override
      public boolean isEmpty() {
         return attributes.isEmpty() && model.isEmpty();
      }
   }
}
