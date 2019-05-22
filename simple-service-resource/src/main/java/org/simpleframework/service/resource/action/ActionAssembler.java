package org.simpleframework.service.resource.action;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.simpleframework.service.DependencyManager;
import org.simpleframework.service.build.ComponentFinder;
import org.simpleframework.service.build.extract.Extractor;
import org.simpleframework.service.build.extract.ModelExtractor;
import org.simpleframework.service.resource.action.build.ActionBuilder;
import org.simpleframework.service.resource.action.build.ActionScanner;
import org.simpleframework.service.resource.action.build.MethodDispatcherResolver;
import org.simpleframework.service.resource.action.extract.BodyExtractor;
import org.simpleframework.service.resource.action.extract.CookieExtractor;
import org.simpleframework.service.resource.action.extract.HeaderExtractor;
import org.simpleframework.service.resource.action.extract.JsonExtractor;
import org.simpleframework.service.resource.action.extract.PartExtractor;
import org.simpleframework.service.resource.action.extract.PathExtractor;
import org.simpleframework.service.resource.action.extract.QueryExtractor;
import org.simpleframework.service.resource.action.extract.RequestExtractor;
import org.simpleframework.service.resource.action.extract.ResponseExtractor;
import org.simpleframework.service.resource.action.write.BodyWriter;
import org.simpleframework.service.resource.action.write.ByteArrayWriter;
import org.simpleframework.service.resource.action.write.CharacterArrayWriter;
import org.simpleframework.service.resource.action.write.ExceptionWriter;
import org.simpleframework.service.resource.action.write.JsonWriter;
import org.simpleframework.service.resource.action.write.ResponseWriter;
import org.simpleframework.service.resource.action.write.StringWriter;
import org.simpleframework.service.resource.annotation.Intercept;
import org.simpleframework.service.resource.annotation.Path;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;

public class ActionAssembler {
   
   public static Set<Class> classesWithAnnotation(Class<? extends Annotation> annotation) {
      Set<Class> types = new HashSet<Class>();
      Iterator<ClassInfo> iterator = new ClassGraph()
            .enableAllInfo()
            //.disableDirScanning()
            .whitelistPackages("org.ternlang.*")
            .whitelistPaths("..")
            //.verbose()
            .scan()
            .getAllClasses()
            .iterator();
      
     String name = annotation.getName();  
     while(iterator.hasNext()) {
        ClassInfo info = iterator.next();
        
        if(info.hasAnnotation(name)) {
           Class type = info.loadClass();
           types.add(type);
        }
     }
     return types;
   }
   
   public static ActionMatcher assemble(DependencyManager source) {
      //ResourceClassScanner resourceScanner = new ResourceClassScanner();
      //Set<Class> interceptors = resourceScanner.scan(Intercept.class);
      //Set<Class> services = resourceScanner.scan(Path.class);
      
      Set<Class> interceptors = classesWithAnnotation(Intercept.class);
      Set<Class> services = classesWithAnnotation(Path.class);
      
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
