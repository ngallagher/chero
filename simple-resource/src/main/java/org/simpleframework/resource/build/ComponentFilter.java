package org.simpleframework.resource.build;

import java.lang.reflect.Modifier;
import java.util.function.Predicate;

import org.simpleframework.module.build.Argument;
import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.LeastRecentlyUsedCache;

public class ComponentFilter implements Predicate<Argument> {

   private final Cache<Class, Boolean> cache;
   
   public ComponentFilter() {
      this(10000);
   }
   
   public ComponentFilter(int capacity) {
      this.cache = new LeastRecentlyUsedCache<Class, Boolean>();
   }
   
   @Override
   public boolean test(Argument argument) {
      Class type = argument.getType();
      
      if(type != null) {
         return cache.fetch(type, this::test);
      }
      return false;
   }
   
   private boolean test(Class type) {
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
