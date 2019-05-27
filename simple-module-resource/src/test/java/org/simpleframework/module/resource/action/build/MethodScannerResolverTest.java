package org.simpleframework.module.resource.action.build;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.annotation.Required;
import org.simpleframework.module.build.ConstructorScanner;
import org.simpleframework.module.build.MethodScanner;
import org.simpleframework.module.core.AnnotationValidator;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.ComponentStore;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.core.Validator;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ModelExtractor;
import org.simpleframework.module.resource.action.RequestContextBuilder;
import org.simpleframework.module.resource.action.extract.CookieExtractor;
import org.simpleframework.module.resource.action.extract.HeaderExtractor;
import org.simpleframework.module.resource.action.extract.PartExtractor;
import org.simpleframework.module.resource.action.extract.QueryExtractor;
import org.simpleframework.module.resource.action.extract.RequestExtractor;
import org.simpleframework.module.resource.action.extract.ResponseExtractor;
import org.simpleframework.module.resource.annotation.CookieParam;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.annotation.Entity;
import org.simpleframework.module.resource.annotation.Produces;
import org.simpleframework.module.resource.annotation.QueryParam;

import junit.framework.TestCase;

public class MethodScannerResolverTest extends TestCase {

   @Path("/")
   private static class ExampleController {

      @Path("/listPeople")
      public void listPeople(
            @QueryParam("person") String person, 
            @CookieParam("SSOID") String id, 
            Model model) 
      {
         System.err.printf("%s %s %s", person, id, model);
      }
   }

   @Path("/contextPath/")
   private static class ExampleCompositeController {

      @Produces("text/plain")
      @Path
      public void listPeople(
            CompositeParam param, 
            @QueryParam("person") String person, 
            Model model) 
      {
         System.err.printf("%s %s %s %s", param.person, param.id, person, model);
      }

      @Produces("text/plain")
      @Path
      public void listPeople(
            CompositeParam param, 
            @QueryParam("person") String person, 
            @Required @QueryParam("enum") SomeEnum x, 
            Model model) 
      {
         System.err.printf("%s %s %s %s %s", param.person, param.id, person, x, model);
      }
   }

   private static enum SomeEnum {
      X, Y, Z;
   }

   @Entity
   private static class CompositeParam {

      public final String person;
      public final String id;

      public CompositeParam(
            @Required @QueryParam("person") String person, 
            @Required @CookieParam("SSOID") String id) 
      {
         this.person = person;
         this.id = id;
      }
   }

   public void testCompositeController() throws Throwable {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      ComponentManager dependencySystem = new ComponentStore();
      ClassFinder finder = new ClassFinder(ExampleCompositeController.class);
      Validator validator = new AnnotationValidator();
      ComponentFilter filter = new ComponentFilter();
      ConstructorScanner constructorScanner = new ConstructorScanner(dependencySystem, extractors, filter);
      MethodScanner methodScanner = new MethodScanner(dependencySystem, constructorScanner, extractors, filter);
      ActionScanner scanner = new ActionScanner(methodScanner, validator);
      MethodDispatcherResolver resolver = new MethodDispatcherResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/context-path/list-people?person=niall.gallagher@rbs.com&enum=X", "");
      request.setCookie("SSOID", "XYZ");
      request.setCookie("SSOSESSION", "ABC");
      MockResponse response = new MockResponse(System.out);
      Context context = new RequestContextBuilder().build(request, response);
      MethodDispatcher dispatcher = resolver.resolveBest(context);
      dispatcher.execute(context);
   }

   public void testSimpleController() throws Throwable {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      ComponentManager dependencySystem = new ComponentStore();
      ClassFinder finder = new ClassFinder(ExampleController.class);
      Validator validator = new AnnotationValidator();
      ComponentFilter filter = new ComponentFilter();
      ConstructorScanner constructorScanner = new ConstructorScanner(dependencySystem, extractors, filter);
      MethodScanner methodScanner = new MethodScanner(dependencySystem, constructorScanner, extractors, filter);
      ActionScanner scanner = new ActionScanner(methodScanner, validator);
      MethodDispatcherResolver resolver = new MethodDispatcherResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/list-people?person=niall.gallagher@rbs.com", "");
      request.setCookie("SSOID", "XYZ");
      request.setCookie("SSOSESSION", "ABC");
      MockResponse response = new MockResponse(System.out);
      Context context = new RequestContextBuilder().build(request, response);
      MethodDispatcher dispatcher = resolver.resolveBest(context);
      dispatcher.execute(context);
   }
}
