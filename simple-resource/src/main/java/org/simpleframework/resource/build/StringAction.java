package org.simpleframework.resource.build;

import org.simpleframework.module.core.Context;
import org.simpleframework.resource.action.Action;

public class StringAction implements Action {
   
   private final String name;
   
   public StringAction(String name) {
      this.name = name;
   }

   @Override
   public Object execute(Context context) {
      return name;
   }

}
