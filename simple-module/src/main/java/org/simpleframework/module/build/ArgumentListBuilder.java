package org.simpleframework.module.build;

import java.lang.reflect.Executable;
import java.util.List;
import java.util.function.Predicate;

import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.extract.Extractor;

public class ArgumentListBuilder {
  
   private final ExtractorResolver resolver;
   private final ParameterScanner scanner;
   private final Extractor[] empty;
   private final Parameter[] none;
   
   public ArgumentListBuilder(ComponentManager manager, ConstructorScanner scanner, List<Extractor> extractors, Predicate<Argument> transients) {
      this.resolver = new ExtractorResolver(manager, scanner, extractors, transients);
      this.scanner = new ParameterScanner();
      this.empty = new Extractor[] {};
      this.none = new Parameter[] {};
   }

   public ArgumentList createArguments(Executable executable) throws Exception {
      Parameter[] parameters = scanner.createParameters(executable);

      if (parameters.length > 0) {
         Extractor[] extractors = new Extractor[parameters.length];

         for (int i = 0; i < parameters.length; i++) {
            extractors[i] = resolver.resolve(parameters[i]);
            
            if(extractors[i] == null) {
               return null;
            }
         }
         return new ArgumentList(extractors, parameters);
      }
      return new ArgumentList(empty, none);
   }
}
