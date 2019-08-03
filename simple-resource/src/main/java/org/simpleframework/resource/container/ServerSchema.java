package org.simpleframework.resource.container;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.simpleframework.module.core.Context;
import org.simpleframework.resource.action.Operation;
import org.simpleframework.resource.action.Schema;

class ServerSchema implements Schema {
   
   private final Function<String, Set<Operation>> builder;
   private final Map<String, Set<Operation>> operations;
   private final Context context;
   
   public ServerSchema(Context context) {
      this.builder = type -> new HashSet<>();
      this.operations = new HashMap<>();
      this.context = context;
   }
   
   @Override
   public Set<Operation> getOperations(String method) {
      return operations.computeIfAbsent(method, builder);
   }
   
   @Override
   public Object getAttribute(String name) {
      return context.getModel().get(name);
   }
}
