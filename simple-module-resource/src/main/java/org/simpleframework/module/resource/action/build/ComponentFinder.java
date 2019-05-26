package org.simpleframework.module.resource.action.build;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.simpleframework.module.graph.ClassPath;

public class ComponentFinder extends ClassFinder {
   
   private final Class<? extends Annotation> type;
   private final ClassPath path;

   public ComponentFinder(ClassPath path, Class<? extends Annotation> type) {
      this.path = path;
      this.type = type;
   }

   @Override
   public Set<Class> getComponents() {
      return path.getTypes(type);
   }
}
