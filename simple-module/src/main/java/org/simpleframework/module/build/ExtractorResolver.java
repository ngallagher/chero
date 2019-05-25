package org.simpleframework.module.build;

import java.util.List;
import java.util.function.Predicate;

import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.extract.ComponentExtractor;
import org.simpleframework.module.extract.DependencyExtractor;
import org.simpleframework.module.extract.DependencyListExtractor;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.StringConverter;

public class ExtractorResolver {

   private final Predicate<Argument> transients;
   private final List<Extractor> extractors;
   private final ConstructorScanner scanner;
   private final ComponentManager manager;
   private final StringConverter converter;

   public ExtractorResolver(ComponentManager manager, ConstructorScanner scanner, List<Extractor> extractors) {
      this(manager, scanner, extractors, argument -> false);
   }
   
   public ExtractorResolver(ComponentManager manager, ConstructorScanner scanner, List<Extractor> extractors, Predicate<Argument> transients) {
      this.converter = new StringConverter();
      this.extractors = extractors;
      this.transients = transients;
      this.scanner = scanner;
      this.manager = manager;
   }
   
   public Extractor resolve(Argument argument) throws Exception {
      for (Extractor extractor : extractors) {
         if (extractor.accept(argument)) {
            return extractor;
         }
      }
      return create(argument);
   }

   private Extractor create(Argument argument) throws Exception {
      Class type = argument.getType();
      Class entry = argument.getEntry();
      
      if (!converter.accept(type)) {
         if (transients.test(argument)) {
            List<Function> builders = scanner.createConstructors(type);
            
            if(!builders.isEmpty()) {
               return new ComponentExtractor(builders, type);
            }
            return null;
         }
         if(argument.isCollection()) {
            return new DependencyListExtractor(manager, entry);
         }   
         return new DependencyExtractor(manager, type);
      }
      return null;
   }
}
