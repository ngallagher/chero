package org.simpleframework.module.resource.action.build;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.simpleframework.module.build.Function;
import org.simpleframework.module.build.Parameter;

public class MethodDescription implements ActionDescription {
   
   private final MethodMatcher matcher;
   private final MethodHeader header;
   private final Function function;
   
   public MethodDescription(MethodMatcher matcher, MethodHeader header, Function function) {
      this.function = function;
      this.header = header;
      this.matcher = matcher;
   }
   
   @Override
   public String getTarget() {
      String method = matcher.verb();
      String pattern = matcher.pattern();
      
      return String.format("%s %s", method, pattern);
   }
   
   @Override
   public List<Parameter> getParameters() {
      Parameter[] parameters = function.getParameters();
      
      if(parameters.length > 0) {
         return Arrays.asList(parameters);
      }
      return Collections.emptyList();
   }
   
   @Override
   public Map<String, String> getHeaders() {
      return header.headers();
   }
   
   @Override
   public String getDescription() {
      return function.getName();
   }

   @Override
   public String getMethod() {
      return matcher.verb();
   }

   @Override
   public String getPath() {
      return matcher.pattern();
   }
}
