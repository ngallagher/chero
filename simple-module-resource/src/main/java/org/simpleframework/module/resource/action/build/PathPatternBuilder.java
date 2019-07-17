package org.simpleframework.module.resource.action.build;

import org.simpleframework.module.build.Function;
import org.simpleframework.module.resource.annotation.Ignore;

public class PathPatternBuilder {   

   private final PathFormatter formatter;

   public PathPatternBuilder() {
      this.formatter = new PathFormatter();
   }
   
   public PathPattern createPattern(Function function, String typePath, String methodPath) throws Exception {
      String methodName = function.getName();
      
      if (!methodPath.equals("") && !methodPath.equals("/")) {
         return compilePattern(function, typePath, methodPath);
      }
      return compilePattern(function, typePath, methodName); 
   }
   
   private PathPattern compilePattern(Function function, String typePath, String methodPath) throws Exception {
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
      return new PathPattern(ignorePath, parentPath, realPath);
   }
}
