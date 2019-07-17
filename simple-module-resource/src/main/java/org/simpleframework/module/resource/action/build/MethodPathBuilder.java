package org.simpleframework.module.resource.action.build;

import org.simpleframework.module.build.Function;
import org.simpleframework.module.resource.annotation.Ignore;

public class MethodPathBuilder {   

   private final PathFormatter formatter;

   public MethodPathBuilder() {
      this.formatter = new PathFormatter();
   }
   
   public MethodPath createPath(Function function, String typePath, String methodPath) throws Exception {
      String methodName = function.getName();
      
      if (!methodPath.equals("") && !methodPath.equals("/")) {
         return resolvePath(function, typePath, methodPath);
      }
      return resolvePath(function, typePath, methodName); 
   }
   
   private MethodPath resolvePath(Function function, String typePath, String methodPath) throws Exception {
      Ignore ignore = function.getAnnotation(Ignore.class);
      String realPath = formatter.formatPath(methodPath);
      String parentPath = "/";
      String ignorePath = "";
      
      if (ignore != null) {
         ignorePath = ignore.value();
         ignorePath = formatter.formatPath(ignorePath);
      }
      if (typePath != null) {
         parentPath = formatter.formatPath(typePath);
      }
      return new MethodPath(ignorePath, parentPath, realPath);
   }
}
