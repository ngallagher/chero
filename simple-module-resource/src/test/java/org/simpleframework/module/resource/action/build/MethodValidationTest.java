package org.simpleframework.module.resource.action.build;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.annotation.Required;
import org.simpleframework.module.build.ComponentFinder;
import org.simpleframework.module.build.extract.Extractor;
import org.simpleframework.module.build.extract.ModelExtractor;
import org.simpleframework.module.common.ComponentManager;
import org.simpleframework.module.common.DependencyManager;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.Model;
import org.simpleframework.module.resource.action.ActionContextBuilder;
import org.simpleframework.module.resource.action.build.ActionScanner;
import org.simpleframework.module.resource.action.build.MethodDispatcher;
import org.simpleframework.module.resource.action.build.MethodDispatcherResolver;
import org.simpleframework.module.resource.action.extract.CookieExtractor;
import org.simpleframework.module.resource.action.extract.HeaderExtractor;
import org.simpleframework.module.resource.action.extract.PartExtractor;
import org.simpleframework.module.resource.action.extract.QueryExtractor;
import org.simpleframework.module.resource.action.extract.RequestExtractor;
import org.simpleframework.module.resource.action.extract.ResponseExtractor;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.annotation.Payload;
import org.simpleframework.module.resource.annotation.QueryParam;

import junit.framework.TestCase;

public class MethodValidationTest extends TestCase {

   @Payload
   public static class SomeComponentWithNoInstantiation {
      private String x;

      public SomeComponentWithNoInstantiation(String x) {
         this.x = x;
      }
   }

   @Payload
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
      ComponentFinder finder = new ComponentFinder(SomeExampleController.class);
      ActionScanner scanner = new ActionScanner(dependencySystem, extractors);
      MethodDispatcherResolver resolver = new MethodDispatcherResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/test/execute?a=XX&z=DD", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new ActionContextBuilder().build(request, response);
      MethodDispatcher dispatcher = resolver.resolveBest(context);
      dispatcher.execute(context);

      assertFalse(context.getValidation().isValid());
      assertTrue(context.getModel().isEmpty());
      assertEquals(context.getValidation().getErrors().size(), 1);
   }

   public void testWithNothingAtAll() throws Throwable {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencyManager dependencySystem = new ComponentManager();
      ComponentFinder finder = new ComponentFinder(SomeExampleController.class);
      ActionScanner scanner = new ActionScanner(dependencySystem, extractors);
      MethodDispatcherResolver resolver = new MethodDispatcherResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/test/execute", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new ActionContextBuilder().build(request, response);
      MethodDispatcher dispatcher = resolver.resolveBest(context);
      dispatcher.execute(context);

      assertFalse(context.getValidation().isValid());
      assertTrue(context.getModel().isEmpty());
      assertEquals(context.getValidation().getErrors().size(), 2);

   }
}
