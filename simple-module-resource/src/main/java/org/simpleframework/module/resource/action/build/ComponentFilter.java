package org.simpleframework.module.resource.action.build;

import java.lang.reflect.Modifier;
import java.util.function.Predicate;

import org.simpleframework.module.build.Argument;

public class ComponentFilter implements Predicate<Argument> {

   @Override
   public boolean test(Argument argument) {
      Class type = argument.getType();
      ComponentType component = ComponentType.resolveType(type);
      int modifiers = type.getModifiers();

      if (component != null) {
         if (Modifier.isAbstract(modifiers)) {
            return false;
         }
         if (Modifier.isInterface(modifiers)) {
            return false;
         }
         return true;
      }
      return false;
   }

}
