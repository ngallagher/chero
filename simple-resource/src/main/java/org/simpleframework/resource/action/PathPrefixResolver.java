package org.simpleframework.resource.action;

import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;

public class PathPrefixResolver implements ActionResolver {

   private final Map<String, Action> actions;
   private final Action fallback;

   public PathPrefixResolver(Map<String, Action> actions) {
      this(actions, null);
   }

   public PathPrefixResolver(Map<String, Action> actions, Action fallback) {
      this.fallback = fallback;
      this.actions = actions;
   }

   public Action resolve(Context context) {
      Set<String> prefixes = actions.keySet();
      
      if(!prefixes.isEmpty()) {
         Model model = context.getModel();
         Request request = model.get(Request.class);
         Path path = request.getPath();
         String normal = path.getPath();
   
         for (String prefix : prefixes) {
            if (normal.startsWith(prefix)) {
               return actions.get(prefix);
            }
         }
      }
      return fallback;
   }
}
