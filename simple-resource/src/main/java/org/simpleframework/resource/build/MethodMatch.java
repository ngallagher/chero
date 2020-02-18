package org.simpleframework.resource.build;

public interface MethodMatch {
   Iterable<MethodDispatcher> actions();
   boolean matches(String path);
   MethodPattern pattern();
}
