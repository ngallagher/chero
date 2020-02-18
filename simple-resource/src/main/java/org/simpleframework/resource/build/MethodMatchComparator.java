package org.simpleframework.resource.build;

import java.util.Comparator;

public class MethodMatchComparator implements Comparator<MethodMatch> {

   @Override
   public int compare(MethodMatch left, MethodMatch right) {
      MethodPattern leftPattern = left.pattern();
      MethodPattern rightPattern = right.pattern();
      boolean leftExpression = leftPattern.isExpression();
      boolean rightExpression = rightPattern.isExpression();
      int compare = Boolean.compare(leftExpression, rightExpression);
      
      if(compare == 0) {
         int leftLength = leftPattern.length();
         int rightLength = rightPattern.length();
         
         return Integer.compare(rightLength, leftLength);
      }
      return compare;
     
   }

}
