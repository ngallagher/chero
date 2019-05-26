package org.simpleframework.module.path;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;

public interface ClassPath {
   ClassNode getType(String name);
   Set<ClassNode> getTypes(Class<? extends Annotation> type);
   Set<ClassNode> getTypes(Predicate<ClassNode> filter);
   Set<ClassNode> getTypes();     
   Set<String> getPackages();   
}
