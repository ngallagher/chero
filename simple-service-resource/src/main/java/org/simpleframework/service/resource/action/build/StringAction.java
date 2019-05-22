package org.simpleframework.service.resource.action.build;

import org.simpleframework.service.context.Context;
import org.simpleframework.service.resource.action.Action;

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
