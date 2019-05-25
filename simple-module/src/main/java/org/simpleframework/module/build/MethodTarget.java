package org.simpleframework.module.build;

import java.util.List;

import org.simpleframework.module.context.Context;

public class MethodTarget {

   private final ConstructorScanner scanner;
   private final Class type;

   public MethodTarget(ConstructorScanner scanner, Class type) {
      this.scanner = scanner;
      this.type = type;
   }
   
   public Object build(Context context) throws Exception {
      List<Function> constructors = scanner.createConstructors(type);
      int length = constructors.size();
      
      if(length > 0) {
         Function match = constructors.get(0);
         float best = 0f;
   
         for (int i = 0; i < length; i++) {
            Function constructor = constructors.get(i);
            float score = constructor.getScore(context);
   
            if (score > best) {
               match = constructor;
               best = score;
            }
         }
         return match.getValue(context);
      }
      return null;
   }
}
