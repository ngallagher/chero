package org.simpleframework.module.resource.action.build;

public interface MethodMatch extends Comparable<MethodMatch> {
   Iterable<MethodDispatcher> actions();
   boolean matches(String path);
   String expression();
   int length();
}
