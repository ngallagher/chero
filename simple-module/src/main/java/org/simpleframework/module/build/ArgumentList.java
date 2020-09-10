package org.simpleframework.module.build;

import org.simpleframework.module.core.Context;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.StringConverter;

public class ArgumentList {

   private final StringConverter converter;
   private final Extractor[] extractors;
   private final Parameter[] parameters;

   public ArgumentList(Extractor[] extractors, Parameter[] parameters) {
      this.converter = new StringConverter();
      this.extractors = extractors;
      this.parameters = parameters;
   }

   public Parameter[] require() {
      Parameter[] types = new Parameter[parameters.length];

      for (int i = 0; i < parameters.length; i++) {
         types[i] = parameters[i];
      }
      return types;
   }

   public Object[] create(Context context) throws Exception {
      Object[] arguments = new Object[extractors.length];

      for (int i = 0; i < extractors.length; i++) {
         Extractor extractor = extractors[i];
         Parameter parameter = parameters[i];

         if (extractor != null) {
            arguments[i] = extractor.extract(parameter, context);
         }
         if (arguments[i] == null) {
            Class type = parameter.getType();

            if (parameter.isRequired()) {
               throw new IllegalArgumentException("Could not resolve " + type);
            }
            if (type.isPrimitive()) {
               arguments[i] = converter.box(type);
            }
         }
      }
      return arguments;
   }

   public float score(Context context) throws Exception {
      float score = 0f;

      for (int i = 0; i < extractors.length; i++) {
         Extractor extractor = extractors[i];
         Parameter parameter = parameters[i];

         if (extractor != null) {
            float result = extractor.score(parameter, context);

            if (result == -1) {
               return -1;
            } else {
               score += result;
            }
         }
      }
      return adjust(score);
   }

   private float adjust(float score) throws Exception {
      float adjustment = parameters.length / 1000.0f;
      float result = score + 1;

      if (result < 1) {
         return result;
      }
      return result - adjustment;
   }
}
