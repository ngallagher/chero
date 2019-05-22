package org.simpleframework.module.resource.action.build;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.service.ComponentManager;
import org.simpleframework.service.DependencyManager;
import org.simpleframework.service.build.ComponentFinder;
import org.simpleframework.service.build.extract.Extractor;
import org.simpleframework.service.build.extract.ModelExtractor;
import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.Model;
import org.simpleframework.service.resource.action.Action;
import org.simpleframework.service.resource.action.ActionContextBuilder;
import org.simpleframework.service.resource.action.build.ActionBuilder;
import org.simpleframework.service.resource.action.build.ActionScanner;
import org.simpleframework.service.resource.action.build.MethodDispatcherResolver;
import org.simpleframework.service.resource.action.extract.CookieExtractor;
import org.simpleframework.service.resource.action.extract.HeaderExtractor;
import org.simpleframework.service.resource.action.extract.PartExtractor;
import org.simpleframework.service.resource.action.extract.QueryExtractor;
import org.simpleframework.service.resource.action.extract.RequestExtractor;
import org.simpleframework.service.resource.action.extract.ResponseExtractor;
import org.simpleframework.service.resource.annotation.GET;
import org.simpleframework.service.resource.annotation.Intercept;
import org.simpleframework.service.resource.annotation.Path;

import junit.framework.TestCase;

public class ActionBuilderTest extends TestCase {

   @Intercept("/a/b/c")
   public static class InterceptorA {
      @Intercept(".*")
      public void addString(Model model) {
         model.set("a", "A");
      }
   }

   @Intercept("/a")
   public static class InterceptorB {
      @Intercept(".*")
      public void addString(Model model) {
         model.set("b", "B");
      }
   }

   @Path("/a/b/c")
   public static class ServiceA {
      
      @GET
      @Path
      public void showA(Model model) {
         model.set("method", "showA");
      }

      @GET
      @Path
      public void showB(Model model) {
         model.set("method", "showB");
      }
   }

   @Path("/a/bad/service")
   public static class ServiceB {
      
      @GET
      @Path
      public void throwException(Model model) {
         throw new IllegalStateException("Catch me!!");
      }
   }

   @Path("/interpolate/exception")
   public static class ServiceC {
      
      @GET
      @Path
      public void throwException(Model model) {
         throw new IllegalStateException("Catch me!!");
      }
   }

   public void testActionBuilder() throws Throwable {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencyManager dependencySystem = new ComponentManager();
      ComponentFinder interceptorFinder = new ComponentFinder(InterceptorA.class, InterceptorB.class);
      ComponentFinder serviceFinder = new ComponentFinder(ServiceA.class, ServiceB.class, ServiceC.class);
      ActionScanner scanner = new ActionScanner(dependencySystem, extractors);
      MethodDispatcherResolver interceptorResolver = new MethodDispatcherResolver(scanner, interceptorFinder);
      MethodDispatcherResolver serviceResolver = new MethodDispatcherResolver(scanner, serviceFinder);
      ActionBuilder builder = new ActionBuilder(serviceResolver, interceptorResolver);

      // Test resolution on showA
      MockRequest request = new MockRequest("GET", "/a/b/c/show-a?x=X&y=Y", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new ActionContextBuilder().build(request, response);
      Action action = builder.resolve(context);
      Object result = action.execute(context);

      assertNotNull(action);
      assertTrue(context.getValidation().isValid());
      assertEquals(context.getModel().get("a"), "A"); 
      assertEquals(context.getModel().get("b"), "B"); 
      assertEquals(context.getModel().get("method"), "showA");

      // Test resolution on showB
      MockRequest secondRequest = new MockRequest("GET", "/a/b/c/show-b?x=X&y=Y", "");
      MockResponse secondResponse = new MockResponse(System.out);
      Context secondContext = new ActionContextBuilder().build(secondRequest, secondResponse);
      Action secondAction = builder.resolve(secondContext);
      Object secondResult = secondAction.execute(secondContext);

      assertNotNull(action);
      assertTrue(secondContext.getValidation().isValid());
      assertEquals(secondContext.getModel().get("a"), "A");
      assertEquals(secondContext.getModel().get("b"), "B"); 
      assertEquals(secondContext.getModel().get("method"), "showB");
                                                    // class

      // Test resolution on throwException
      MockRequest thirdRequest = new MockRequest("GET", "/a/bad/service/throw-exception?x=X&y=Y", "");
      MockResponse thirdResponse = new MockResponse(System.out);
      Context thirdContext = new ActionContextBuilder().build(thirdRequest, thirdResponse);
      Action thirdAction = builder.resolve(thirdContext);
      Object thirdResult = thirdAction.execute(thirdContext);

      assertNotNull(action);
      assertTrue(thirdContext.getValidation().isValid());
      assertEquals(thirdContext.getModel().get("b"), "B"); 
      assertEquals(thirdContext.getError().getCause().getMessage(), "Catch me!!");

      // Test resolution on throwException with interpolation
      MockRequest fourthRequest = new MockRequest("GET", "/interpolate/exception/throw-exception?x=X&y=Y", "");
      MockResponse fourthResponse = new MockResponse(System.out);
      Context fourthContext = new ActionContextBuilder().build(fourthRequest, fourthResponse);
      Action fourthAction = builder.resolve(fourthContext);
      Object fourthResult = fourthAction.execute(fourthContext);

      assertNotNull(action);
      assertTrue(fourthContext.getValidation().isValid());
      assertEquals(fourthContext.getModel().get("a"), null);
      assertEquals(fourthContext.getModel().get("b"), null); 
      assertEquals(fourthContext.getError().getCause().getMessage(), "Catch me!!"); 
   }

}
