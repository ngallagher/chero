package org.simpleframework.module.resource.container;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.simpleframework.module.core.Context;
import org.simpleframework.module.resource.action.ActionDescription;
import org.simpleframework.module.resource.action.Schema;

class ServerSchema implements Schema {
   
   private final Function<String, Set<ActionDescription>> builder;
   private final Map<String, Set<ActionDescription>> actions;
   private final Context context;
   
   public ServerSchema(Context context) {
      this.builder = type -> new HashSet<>();
      this.actions = new HashMap<>();
      this.context = context;
   }
   
   @Override
   public Set<ActionDescription> getActions(String method) {
      return actions.computeIfAbsent(method, builder);
   }
   
   @Override
   public Object getAttribute(String name) {
      return context.getModel().get(name);
   }
}
