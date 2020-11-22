package org.simpleframework.module.path;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;

public interface ClassPath {
   ClassNode findType(String name);
   Set<MethodNode> findMethods(Class<? extends Annotation> type);
   Set<MethodNode> findMethods(Predicate<MethodNode> filter);
   Set<ClassNode> findTypes(Class<? extends Annotation> type);
   Set<ClassNode> findTypes(Predicate<ClassNode> filter);
   Set<ClassNode> findTypes();
   Set<String> findPackages();
}
