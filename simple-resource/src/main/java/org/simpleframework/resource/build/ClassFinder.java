package org.simpleframework.resource.build;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClassFinder {

   private final Set<Class> types;

   public ClassFinder(Class... types) {
      this(Arrays.asList(types));
   }

   public ClassFinder(Collection<Class> types) {
      this.types = new LinkedHashSet<Class>(types);
   }

   public Set<Class> getComponents() {
      return types;
   }
}
