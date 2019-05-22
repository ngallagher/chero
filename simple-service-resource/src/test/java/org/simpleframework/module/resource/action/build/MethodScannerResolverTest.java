package org.simpleframework.module.resource.action.build;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.service.ComponentManager;
import org.simpleframework.service.DependencyManager;
import org.simpleframework.service.annotation.Component;
import org.simpleframework.service.annotation.Required;
import org.simpleframework.service.build.ComponentFinder;
import org.simpleframework.service.build.extract.Extractor;
import org.simpleframework.service.build.extract.ModelExtractor;
import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.Model;
import org.simpleframework.service.resource.action.ActionContextBuilder;
import org.simpleframework.service.resource.action.build.ActionScanner;
import org.simpleframework.service.resource.action.build.MethodDispatcher;
import org.simpleframework.service.resource.action.build.MethodDispatcherResolver;
import org.simpleframework.service.resource.action.extract.CookieExtractor;
import org.simpleframework.service.resource.action.extract.HeaderExtractor;
import org.simpleframework.service.resource.action.extract.PartExtractor;
import org.simpleframework.service.resource.action.extract.QueryExtractor;
import org.simpleframework.service.resource.action.extract.RequestExtractor;
import org.simpleframework.service.resource.action.extract.ResponseExtractor;
import org.simpleframework.service.resource.annotation.CookieParam;
import org.simpleframework.service.resource.annotation.Path;
import org.simpleframework.service.resource.annotation.Produces;
import org.simpleframework.service.resource.annotation.QueryParam;

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

   @Component
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
      DependencyManager dependencySystem = new ComponentManager();
      ComponentFinder finder = new ComponentFinder(ExampleCompositeController.class);
      ActionScanner scanner = new ActionScanner(dependencySystem, extractors);
      MethodDispatcherResolver resolver = new MethodDispatcherResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/context-path/list-people?person=niall.gallagher@rbs.com&enum=X", "");
      request.setCookie("SSOID", "XYZ");
      request.setCookie("SSOSESSION", "ABC");
      MockResponse response = new MockResponse(System.out);
      Context context = new ActionContextBuilder().build(request, response);
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
      DependencyManager dependencySystem = new ComponentManager();
      ComponentFinder finder = new ComponentFinder(ExampleController.class);
      ActionScanner scanner = new ActionScanner(dependencySystem, extractors);
      MethodDispatcherResolver resolver = new MethodDispatcherResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/list-people?person=niall.gallagher@rbs.com", "");
      request.setCookie("SSOID", "XYZ");
      request.setCookie("SSOSESSION", "ABC");
      MockResponse response = new MockResponse(System.out);
      Context context = new ActionContextBuilder().build(request, response);
      MethodDispatcher dispatcher = resolver.resolveBest(context);
      dispatcher.execute(context);
   }
}
