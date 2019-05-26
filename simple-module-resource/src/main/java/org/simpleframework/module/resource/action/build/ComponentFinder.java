package org.simpleframework.module.resource.action.build;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

import org.simpleframework.module.graph.index.ClassNode;
import org.simpleframework.module.graph.index.ClassPath;

public class ComponentFinder extends ClassFinder {
   
   private final Class<? extends Annotation> type;
   private final ClassPath path;

   public ComponentFinder(ClassPath path, Class<? extends Annotation> type) {
      this.path = path;
      this.type = type;
   }

   @Override
   public Set<Class> getComponents() {
      return path.getTypes(type)
            .stream()
            .map(ClassNode::getType)
            .collect(Collectors.toSet());
   }
}
