package org.simpleframework.module.resource.action.build;

import org.simpleframework.module.annotation.Required;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.resource.annotation.Entity;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.annotation.QueryParam;

import junit.framework.TestCase;

public class MethodValidationTest extends TestCase {

   @Entity
   public static class SomeComponentWithNoInstantiation {
      private String x;

      public SomeComponentWithNoInstantiation(String x) {
         this.x = x;
      }
   }

   @Entity
   public static class SomeComponent {
      private String y;

      public SomeComponent(@Required @QueryParam("z") String y) {
         this.y = y;
      }
   }

   @Path("/test")
   public static class SomeExampleController {

      private SomeComponentWithNoInstantiation shouldBeNull;
      private SomeComponent component;
      private String x;
      private Integer d;

      @Path
      public void execute(
            Model model, 
            SomeComponent component, 
            SomeComponentWithNoInstantiation shouldBeNull, 
            @Required @QueryParam("a") String x,
            @Required @QueryParam("y") Integer d) 
      {
         this.component = component;
         this.x = x;
         this.d = d;
         model.set("this", this);
      }
   }
   
   public void testTest() {}

//   public void testCompositeController() throws Throwable {
//      List<Extractor> extractors = new LinkedList<Extractor>();
//      extractors.add(new RequestExtractor());
//      extractors.add(new ResponseExtractor());
//      extractors.add(new ModelExtractor());
//      extractors.add(new QueryExtractor());
//      extractors.add(new CookieExtractor());
//      extractors.add(new HeaderExtractor());
//      extractors.add(new PartExtractor());
//      ComponentManager dependencySystem = new ComponentStore();
//      ClassFinder finder = new ClassFinder(SomeExampleController.class);
//      Validator validator = new AnnotationValidator();
//      ComponentFilter filter = new ComponentFilter();
//      ConstructorScanner constructorScanner = new ConstructorScanner(dependencySystem, extractors, filter);
//      MethodScanner methodScanner = new MethodScanner(dependencySystem, constructorScanner, extractors, filter);
//      ActionScanner scanner = new ActionScanner(methodScanner, validator);
//      MethodDispatcherResolver resolver = new MethodDispatcherResolver(scanner, finder);
//      MockRequest request = new MockRequest("GET", "/test/execute?a=XX&z=DD", "");
//      MockResponse response = new MockResponse(System.out);
//      Context context = new ActionContextBuilder().build(request, response);
//      MethodDispatcher dispatcher = resolver.resolveBest(context);
//      dispatcher.execute(context);
//
//      assertFalse(context.getValidation().isValid());
//      assertTrue(context.getModel().isEmpty());
//      assertEquals(context.getValidation().getErrors().size(), 1);
//   }
//
//   public void testWithNothingAtAll() throws Throwable {
//      List<Extractor> extractors = new LinkedList<Extractor>();
//      extractors.add(new RequestExtractor());
//      extractors.add(new ResponseExtractor());
//      extractors.add(new ModelExtractor());
//      extractors.add(new QueryExtractor());
//      extractors.add(new CookieExtractor());
//      extractors.add(new HeaderExtractor());
//      extractors.add(new PartExtractor());
//      ComponentManager dependencySystem = new ComponentStore();
//      ClassFinder finder = new ClassFinder(SomeExampleController.class);
//      Validator validator = new AnnotationValidator();
//      ComponentFilter filter = new ComponentFilter();
//      ConstructorScanner constructorScanner = new ConstructorScanner(dependencySystem, extractors, filter);
//      MethodScanner methodScanner = new MethodScanner(dependencySystem, constructorScanner, extractors, filter);
//      ActionScanner scanner = new ActionScanner(methodScanner, validator);
//      MethodDispatcherResolver resolver = new MethodDispatcherResolver(scanner, finder);
//      MockRequest request = new MockRequest("GET", "/test/execute", "");
//      MockResponse response = new MockResponse(System.out);
//      Context context = new ActionContextBuilder().build(request, response);
//      MethodDispatcher dispatcher = resolver.resolveBest(context);
//      dispatcher.execute(context);
//
//      assertFalse(context.getValidation().isValid());
//      assertTrue(context.getModel().isEmpty());
//      assertEquals(context.getValidation().getErrors().size(), 2);
//
//   }
}
