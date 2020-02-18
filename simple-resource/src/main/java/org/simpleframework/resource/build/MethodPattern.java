package org.simpleframework.resource.build;

import java.util.regex.Pattern;

public class MethodPattern {

   private final Pattern pattern;
   private final String path;
   private final boolean expression;
   
   public MethodPattern(String pattern, boolean expression) {
      this.pattern = Pattern.compile(pattern);
      this.expression = expression;
      this.path = pattern;
   }
   
   public boolean isExpression() {
      return expression;
   }
   
   public String path() {
      return path;
   }
   
   public Pattern pattern() {
      return pattern;
   }
   
   public int length() {
      return path.length();
   }
   
   @Override
   public int hashCode() {
      return pattern.hashCode();
   }
   
   @Override
   public boolean equals(Object other) {
      if(other == this) {
         return true;
      }
      if(other == null) {
         return false;
      }
      if(other instanceof MethodPattern) {
         return equals((MethodPattern)other);
      }
      return false;
   }
   
   public boolean equals(MethodPattern other) {
      if(other.expression == expression) {
         return other.path.equals(path); 
      }
      return false;
   }
   
   @Override
   public String toString() {
      return path;
   }
}
