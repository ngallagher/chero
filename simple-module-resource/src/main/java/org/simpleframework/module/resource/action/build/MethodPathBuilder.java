package org.simpleframework.module.resource.action.build;

public class MethodPathBuilder {   

   private final PathFormatter formatter;

   public MethodPathBuilder() {
      this.formatter = new PathFormatter();
   }
   
   public String create(String path, String name) {
      if (!path.equals("") && !path.equals("/")) {
         return formatter.formatPath(path);  
      } 
      return formatter.formatPath(name);
   }
}
