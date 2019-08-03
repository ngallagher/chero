package org.simpleframework.resource.action;

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
import org.simpleframework.resource.annotation.Filter;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.build.ActionBuilder;
import org.simpleframework.resource.build.ActionScanner;
import org.simpleframework.resource.build.ClassFinder;
import org.simpleframework.resource.build.ComponentFilter;
import org.simpleframework.resource.build.ComponentFinder;
import org.simpleframework.resource.build.MethodDispatcherResolver;
import org.simpleframework.resource.build.MethodMatchIndexer;
import org.simpleframework.resource.extract.BodyExtractor;
import org.simpleframework.resource.extract.CookieExtractor;
import org.simpleframework.resource.extract.HeaderExtractor;
import org.simpleframework.resource.extract.JsonExtractor;
import org.simpleframework.resource.extract.PartExtractor;
import org.simpleframework.resource.extract.PathExtractor;
import org.simpleframework.resource.extract.QueryExtractor;
import org.simpleframework.resource.extract.RequestExtractor;
import org.simpleframework.resource.extract.ResponseExtractor;
import org.simpleframework.resource.extract.XmlExtractor;
import org.simpleframework.resource.write.BodyWriter;
import org.simpleframework.resource.write.ByteArrayWriter;
import org.simpleframework.resource.write.CharacterArrayWriter;
import org.simpleframework.resource.write.CompletableFutureWriter;
import org.simpleframework.resource.write.ExceptionWriter;
import org.simpleframework.resource.write.JsonWriter;
import org.simpleframework.resource.write.ResponseEntityWriter;
import org.simpleframework.resource.write.ResponseWriter;
import org.simpleframework.resource.write.StringWriter;
import org.simpleframework.resource.write.XmlWriter;
import org.simpleframework.xml.core.Persister;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ActionAssembler {
   
   private final ClassFinder serviceFinder;
   private final ClassFinder filterFinder;
   private final ComponentManager manager;
   private final ComponentFilter filter;
   private final Validator validator;
   private final Schema schema;
   
   public ActionAssembler(ComponentManager manager, ClassPath path, Schema schema) {
      this.filterFinder = new ComponentFinder(path, Filter.class);
      this.serviceFinder = new ComponentFinder(path, Path.class);
      this.validator = new AnnotationValidator();
      this.filter = new ComponentFilter();
      this.manager = manager;
      this.schema = schema;
   }
   
   public ActionMatcher assemble(List<Extractor> extractors, List<BodyWriter> writers) {
      Persister persister = new Persister();
      ObjectMapper mapper = new JsonMapper();
      ConstructorScanner constructorScanner = new ConstructorScanner(manager, extractors, filter);
      MethodScanner methodScanner = new MethodScanner(manager, constructorScanner, extractors, filter);
      ActionScanner actionScanner = new ActionScanner(methodScanner, validator);
      MethodMatchIndexer filterIndexer = new MethodMatchIndexer(actionScanner, filterFinder);
      MethodMatchIndexer serviceIndexer = new MethodMatchIndexer(actionScanner, serviceFinder, schema);
      MethodDispatcherResolver filterResolver = new MethodDispatcherResolver(filterIndexer);
      MethodDispatcherResolver serviceResolver = new MethodDispatcherResolver(serviceIndexer);
      ActionResolver resolver = new ActionBuilder(serviceResolver, filterResolver);
      ResponseWriter router = new ResponseWriter(writers);
      
      writers.add(new CompletableFutureWriter(router));
      writers.add(new ResponseEntityWriter(router));
      writers.add(new ByteArrayWriter());
      writers.add(new CharacterArrayWriter());
      writers.add(new JsonWriter(mapper));
      writers.add(new XmlWriter(persister));
      writers.add(new ExceptionWriter());
      writers.add(new StringWriter());      

      extractors.add(new JsonExtractor(mapper));
      extractors.add(new XmlExtractor(persister));
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
