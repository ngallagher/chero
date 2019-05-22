package org.simpleframework.module.common;

import java.util.Map;
import java.util.function.Predicate;

import io.github.classgraph.ClassInfo;

public interface DependencyPath {
   Map<String, ClassInfo> getModules();
   Map<String, ClassInfo> getComponents();
   Map<String, ClassInfo> getObjects();
   Predicate<String> getPredicate();
}
