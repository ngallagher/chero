package org.simpleframework.module.graph;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import io.github.classgraph.ClassInfo;

public interface DependencyPath {
   Set<Class> getTypes(Class<? extends Annotation> type);
   Map<String, ClassInfo> getModules();
   Map<String, ClassInfo> getComponents();
   Map<String, ClassInfo> getObjects();
   Predicate<String> getPredicate();
   
}
