package org.simpleframework.module.graph.index;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;

public interface ClassPath {
   ClassNode getType(String name);
   ClassNode getType(String name, int dimensions);
   Set<ClassNode> getTypes(Class<? extends Annotation> type);
   Set<ClassNode> getTypes(Predicate<ClassNode> filter);
   Set<ClassNode> getTypes();     
   Predicate<String> getPredicate();
   
}
