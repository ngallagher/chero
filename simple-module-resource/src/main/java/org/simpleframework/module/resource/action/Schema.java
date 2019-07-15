package org.simpleframework.module.resource.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.simpleframework.module.resource.action.build.ActionDescription;

public class Schema {

   private final Function<String, Set<ActionDescription>> builder;
   private final Map<String, Set<ActionDescription>> actions;
   private final String description;
   
   public Schema(String description) {
      this.builder = type -> new HashSet<>();
      this.actions = new HashMap<>();
      this.description = description;
   }
   
   public Set<ActionDescription> getActions(String method) {
      return actions.computeIfAbsent(method, builder);
   }
   
   public String getDescription() {
      return description;
   }
}
