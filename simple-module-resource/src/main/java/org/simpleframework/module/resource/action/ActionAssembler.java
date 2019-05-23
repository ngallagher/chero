package org.simpleframework.module.resource.action;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.simpleframework.module.DependencyManager;
import org.simpleframework.module.build.ComponentFinder;
import org.simpleframework.module.build.extract.Extractor;
import org.simpleframework.module.build.extract.ModelExtractor;
import org.simpleframework.module.common.DependencyPath;
import org.simpleframework.module.resource.action.build.ActionBuilder;
import org.simpleframework.module.resource.action.build.ActionScanner;
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
import org.simpleframework.module.resource.action.write.ExceptionWriter;
import org.simpleframework.module.resource.action.write.JsonWriter;
import org.simpleframework.module.resource.action.write.ResponseWriter;
import org.simpleframework.module.resource.action.write.StringWriter;
import org.simpleframework.module.resource.annotation.Intercept;
import org.simpleframework.module.resource.annotation.Path;

public class ActionAssembler {
   
   private final DependencyManager source;
   private final DependencyPath path;
   
   public ActionAssembler(DependencyManager source, DependencyPath path) {
      this.source = source;
      this.path = path;
   }
   
   public ActionMatcher assemble() {
      Set<Class> interceptors = path.getTypes(Intercept.class);
      Set<Class> services = path.getTypes(Path.class);

      List<Extractor> extractors = new LinkedList<Extractor>();
      List<BodyWriter> builders = new LinkedList<BodyWriter>();
      ComponentFinder interceptorFinder = new ComponentFinder(interceptors);
      ComponentFinder serviceFinder = new ComponentFinder(services);
      ActionScanner scanner = new ActionScanner(source, extractors);
      MethodDispatcherResolver interceptorResolver = new MethodDispatcherResolver(scanner, interceptorFinder);
      MethodDispatcherResolver serviceResolver = new MethodDispatcherResolver(scanner, serviceFinder);
      ActionResolver resolver = new ActionBuilder(serviceResolver, interceptorResolver);
      ResponseWriter router = new ResponseWriter(builders);

      builders.add(new JsonWriter());
      builders.add(new ByteArrayWriter());
      builders.add(new CharacterArrayWriter());
      builders.add(new ExceptionWriter());
      builders.add(new StringWriter());

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
      
      return new ActionMatcher(resolver, router);
   }
}
