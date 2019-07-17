package org.simpleframework.module.resource.action.build;

public class PathPattern {

   private final String[] paths;
   private final String ignore;
   
   public PathPattern(String ignore, String... paths) {
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
