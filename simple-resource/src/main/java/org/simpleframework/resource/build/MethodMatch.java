package org.simpleframework.resource.build;

public interface MethodMatch extends Comparable<MethodMatch> {
   Iterable<MethodDispatcher> actions();
   boolean matches(String path);
   String expression();
   int length();
}
