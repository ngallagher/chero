package org.simpleframework.module.graph;

import java.util.Objects;

import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.path.MethodNode;
import org.simpleframework.module.path.ParameterNode;

public class DependencyQualifier {

   public DependencyQualifier() {
      super();
   }

   public String qualify(Function function) {
      Provides annotation = function.getAnnotation(Provides.class);

      if(annotation != null) {
         String value = annotation.value();
         return Objects.equals("", value) ? null : value;
      }
      return null;
   }

   public String qualify(ParameterNode node) {
      Provides annotation = node.getAnnotation(Provides.class);

      if(annotation != null) {
         String value = annotation.value();
         return Objects.equals("", value) ? null : value;
      }
      return null;
   }

   public String qualify(MethodNode node) {
      Provides annotation = node.getAnnotation(Provides.class);

      if(annotation != null) {
         String value = annotation.value();
         return Objects.equals("", value) ? null : value;
      }
      return null;
   }
}
