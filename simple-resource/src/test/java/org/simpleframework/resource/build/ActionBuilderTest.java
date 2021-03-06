package org.simpleframework.resource.build;

import java.util.LinkedList;
import java.util.List;

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
import org.simpleframework.resource.action.Action;
import org.simpleframework.resource.action.RequestContextBuilder;
import org.simpleframework.resource.annotation.Filter;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.build.ActionBuilder;
import org.simpleframework.resource.build.ActionScanner;
import org.simpleframework.resource.build.ClassFinder;
import org.simpleframework.resource.build.ComponentFilter;
import org.simpleframework.resource.build.MethodDispatcherResolver;
import org.simpleframework.resource.build.MethodMatchIndexer;
import org.simpleframework.resource.extract.CookieExtractor;
import org.simpleframework.resource.extract.HeaderExtractor;
import org.simpleframework.resource.extract.PartExtractor;
import org.simpleframework.resource.extract.QueryExtractor;
import org.simpleframework.resource.extract.RequestExtractor;
import org.simpleframework.resource.extract.ResponseExtractor;

import junit.framework.TestCase;

public class ActionBuilderTest extends TestCase {

   @Filter("/a/b/c")
   public static class InterceptorA {
      @Path(".*")
      public void addString(Model model) {
         model.set("a", "A");
      }
   }

   @Filter("/a")
   public static class InterceptorB {
      @Path(".*")
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
      ComponentManager dependencySystem = new ComponentStore();
      ClassFinder interceptorFinder = new ClassFinder(InterceptorA.class, InterceptorB.class);
      ClassFinder serviceFinder = new ClassFinder(ServiceA.class, ServiceB.class, ServiceC.class);
      
      Validator validator = new AnnotationValidator();
      ComponentFilter filter = new ComponentFilter();
      ConstructorScanner constructorScanner = new ConstructorScanner(dependencySystem, extractors, filter);
      MethodScanner methodScanner = new MethodScanner(dependencySystem, constructorScanner, extractors, filter);
      ActionScanner scanner = new ActionScanner(methodScanner, validator);
      
      MethodMatchIndexer interceptorIndexer = new MethodMatchIndexer(scanner, interceptorFinder);
      MethodMatchIndexer serviceIndexer = new MethodMatchIndexer(scanner, serviceFinder);
      MethodDispatcherResolver interceptorResolver = new MethodDispatcherResolver(interceptorIndexer);
      MethodDispatcherResolver serviceResolver = new MethodDispatcherResolver(serviceIndexer);
      ActionBuilder builder = new ActionBuilder(serviceResolver, interceptorResolver);

      // Test resolution on showA
      MockRequest request = new MockRequest("GET", "/a/b/c/show-a?x=X&y=Y", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new RequestContextBuilder().build(request, response);
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
      Context secondContext = new RequestContextBuilder().build(secondRequest, secondResponse);
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
      Context thirdContext = new RequestContextBuilder().build(thirdRequest, thirdResponse);
      Action thirdAction = builder.resolve(thirdContext);
      Object thirdResult = thirdAction.execute(thirdContext);

      assertNotNull(action);
      assertTrue(thirdContext.getValidation().isValid());
      assertEquals(thirdContext.getModel().get("b"), "B"); 
      assertEquals(thirdContext.getError().getCause().getMessage(), "Catch me!!");

      // Test resolution on throwException with interpolation
      MockRequest fourthRequest = new MockRequest("GET", "/interpolate/exception/throw-exception?x=X&y=Y", "");
      MockResponse fourthResponse = new MockResponse(System.out);
      Context fourthContext = new RequestContextBuilder().build(fourthRequest, fourthResponse);
      Action fourthAction = builder.resolve(fourthContext);
      Object fourthResult = fourthAction.execute(fourthContext);

      assertNotNull(action);
      assertTrue(fourthContext.getValidation().isValid());
      assertEquals(fourthContext.getModel().get("a"), null);
      assertEquals(fourthContext.getModel().get("b"), null); 
      assertEquals(fourthContext.getError().getCause().getMessage(), "Catch me!!"); 
   }

}
