package org.simpleframework.module.resource.action.build;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.simpleframework.module.graph.ModuleFilter;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

public class ComponentFinder extends ClassFinder {
   
   private final Class<? extends Annotation> type;
   private final ModuleFilter filter;
   private final ClassPath path;

   public ComponentFinder(ClassPath path, Class<? extends Annotation> type) {
      this.filter = new ModuleFilter(path, Collections.EMPTY_SET);
      this.path = path;
      this.type = type;
   }

   @Override
   public Set<Class> getComponents() {
      return path.getTypes(type)
            .stream()
            .filter(filter::isModule)
            .map(ClassNode::getType)
            .collect(Collectors.toSet());
   }
}
