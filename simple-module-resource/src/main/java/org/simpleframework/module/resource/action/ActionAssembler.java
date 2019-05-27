package org.simpleframework.module.resource.action;

import java.util.List;

import org.simpleframework.module.build.ConstructorScanner;
import org.simpleframework.module.build.MethodScanner;
import org.simpleframework.module.core.AnnotationValidator;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.Validator;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ModelExtractor;
import org.simpleframework.module.extract.ValueExtractor;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.resource.action.build.ActionBuilder;
import org.simpleframework.module.resource.action.build.ActionScanner;
import org.simpleframework.module.resource.action.build.ClassFinder;
import org.simpleframework.module.resource.action.build.ComponentFilter;
import org.simpleframework.module.resource.action.build.ComponentFinder;
import org.simpleframework.module.resource.action.build.MethodDispatcherResolver;
import org.simpleframework.module.resource.action.extract.BodyExtractor;
import org.simpleframework.module.resource.action.extract.CookieExtractor;
import org.simpleframework.module.resource.action.extract.HeaderExtractor;
import org.simpleframework.module.resource.action.extract.JsonExtractor;
import org.simpleframework.module.resource.action.extract.PartExtractor;
import org.simpleframework.module.resource.action.extract.PathExtractor;
import org.simpleframework.module.resource.action.extract.QueryExtractor;
import org.simpleframework.module.resource.action.extract.RequestExtractor;
import org.simpleframework.module.resource.action.extract.ResponseExtractor;
import org.simpleframework.module.resource.action.write.BodyWriter;
import org.simpleframework.module.resource.action.write.ByteArrayWriter;
import org.simpleframework.module.resource.action.write.CharacterArrayWriter;
import org.simpleframework.module.resource.action.write.CompletableFutureWriter;
import org.simpleframework.module.resource.action.write.ExceptionWriter;
import org.simpleframework.module.resource.action.write.JsonWriter;
import org.simpleframework.module.resource.action.write.ResponseEntityWriter;
import org.simpleframework.module.resource.action.write.ResponseWriter;
import org.simpleframework.module.resource.action.write.StringWriter;
import org.simpleframework.module.resource.annotation.Filter;
import org.simpleframework.module.resource.annotation.Path;

public class ActionAssembler {
   
   private final ClassFinder interceptorFinder;
   private final ClassFinder serviceFinder;
   private final ComponentManager manager;
   private final ComponentFilter filter;
   private final Validator validator;
   
   public ActionAssembler(ComponentManager manager, ClassPath path) {
      this.interceptorFinder = new ComponentFinder(path, Filter.class);
      this.serviceFinder = new ComponentFinder(path, Path.class);
      this.validator = new AnnotationValidator();
      this.filter = new ComponentFilter();
      this.manager = manager;
   }
   
   public ActionMatcher assemble(List<Extractor> extractors, List<BodyWriter> writers) {
      ConstructorScanner constructorScanner = new ConstructorScanner(manager, extractors, filter);
      MethodScanner methodScanner = new MethodScanner(manager, constructorScanner, extractors, filter);
      ActionScanner actionScanner = new ActionScanner(methodScanner, validator);
      MethodDispatcherResolver interceptorResolver = new MethodDispatcherResolver(actionScanner, interceptorFinder);
      MethodDispatcherResolver serviceResolver = new MethodDispatcherResolver(actionScanner, serviceFinder);
      ActionResolver resolver = new ActionBuilder(serviceResolver, interceptorResolver);
      ResponseWriter router = new ResponseWriter(writers);

      writers.add(new CompletableFutureWriter(router));
      writers.add(new ResponseEntityWriter(router));
      writers.add(new JsonWriter());
      writers.add(new ByteArrayWriter());
      writers.add(new CharacterArrayWriter());
      writers.add(new ExceptionWriter());
      writers.add(new StringWriter());      

      extractors.add(new JsonExtractor());
      extractors.add(new PathExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new BodyExtractor());
      extractors.add(new ValueExtractor());
      
      return new ActionMatcher(resolver, router);
   }
}
