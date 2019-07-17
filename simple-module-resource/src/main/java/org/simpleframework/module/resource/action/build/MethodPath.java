package org.simpleframework.module.resource.action.build;

public class MethodPath {

   private final String[] paths;
   private final String ignore;
   
   public MethodPath(String ignore, String... paths) {
      this.ignore = ignore;
      this.paths = paths;
   }
   
   public String[] getPaths() {
      return paths;
   }
   
   public String getIgnore() {
      return ignore;
   }
}
