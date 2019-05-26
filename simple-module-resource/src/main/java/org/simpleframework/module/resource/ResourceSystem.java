package org.simpleframework.module.resource;

import java.util.List;

import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.resource.action.ActionAssembler;
import org.simpleframework.module.resource.action.ActionMatcher;

public class ResourceSystem {

   private final List<ResourceMatcher> matchers;
   private final ActionMatcher matcher;
   
   public ResourceSystem(ComponentManager manager, ActionAssembler assembler) {
      this.matchers = manager.resolveAll(ResourceMatcher.class);
      this.matcher = assembler.assemble();
   }

   public ResourceMatcher create() {
      return (request, response) -> {
         Resource resource = matcher.match(request, response);
         
         if(resource == null) {
            for (ResourceMatcher next : matchers) {
               if(next != matcher) {
                  Resource matched = next.match(request, response);
         
                  if (matched != null) {
                     return matched;
                  }
               }
            }
         }
         return resource;
      };
   }
}