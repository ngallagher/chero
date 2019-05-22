package org.simpleframework.module.resource.action.build;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.service.ComponentManager;
import org.simpleframework.service.DependencyManager;
import org.simpleframework.service.annotation.Component;
import org.simpleframework.service.annotation.Inject;
import org.simpleframework.service.annotation.Required;
import org.simpleframework.service.build.ComponentBuilder;
import org.simpleframework.service.build.DependencyScanner;
import org.simpleframework.service.build.extract.Extractor;
import org.simpleframework.service.build.extract.ModelExtractor;
import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.MapContext;
import org.simpleframework.service.resource.action.ActionContextBuilder;
import org.simpleframework.service.resource.action.extract.CookieExtractor;
import org.simpleframework.service.resource.action.extract.HeaderExtractor;
import org.simpleframework.service.resource.action.extract.PartExtractor;
import org.simpleframework.service.resource.action.extract.QueryExtractor;
import org.simpleframework.service.resource.action.extract.RequestExtractor;
import org.simpleframework.service.resource.action.extract.ResponseExtractor;
import org.simpleframework.service.resource.annotation.QueryParam;

import junit.framework.TestCase;

public class ComponentWithNoParametersTest extends TestCase {

   @Component
   public static class SomeComponent {

      String a;
      String b;

      @Required
      @QueryParam("c")
      String c;

      public SomeComponent(@Required @QueryParam("a") String a, @Required @QueryParam("b") String b) {
         this.a = a;
         this.b = b;
      }
   }

   public static class SomeComponentWithSomeComponent extends SomeComponent {

      @Required
      @Inject
      SomeComponent component;

      @Required
      @QueryParam("c")
      String c;

      public SomeComponentWithSomeComponent(@Required @QueryParam("a") String a) {
         super(a, null);
      }
   }

   public void testBuilder() throws Exception {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencyManager dependencySystem = new ComponentManager();
      DependencyScanner scanner = new DependencyScanner(dependencySystem, extractors);
      List<ComponentBuilder> builders = scanner.createBuilders(SomeComponent.class);
      MockRequest request = new MockRequest("GET", "/?a=A", "");
      MockResponse response = new MockResponse();
      Context context = new ActionContextBuilder().build(request, response);
      ComponentBuilder builder = builders.iterator().next();
      Object value = builder.build(context);
      SomeComponent component = (SomeComponent) value;

      System.err.println(context.getValidation().getErrors());

      assertNotNull(value);
      assertEquals(component.a, "A");
      assertEquals(component.b, null);
      assertEquals(component.c, null);
      assertFalse(context.getValidation().isValid());
      assertEquals(context.getValidation().getErrors().size(), 1);
   }

   public void testBuilderWithNoParametersAtAll() throws Exception {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencyManager dependencySystem = new ComponentManager();
      DependencyScanner scanner = new DependencyScanner(dependencySystem, extractors);
      List<ComponentBuilder> builders = scanner.createBuilders(SomeComponentWithSomeComponent.class);
      MockRequest request = new MockRequest("GET", "/", "");
      MockResponse response = new MockResponse();
      Context context = new ActionContextBuilder().build(request, response);
      ComponentBuilder builder = builders.iterator().next();
      Object value = builder.build(context);
      SomeComponentWithSomeComponent component = (SomeComponentWithSomeComponent) value;

      System.err.println(context.getValidation().getErrors());

      assertNotNull(component.component);
      assertEquals(component.component.a, null);
      assertEquals(component.component.b, null);
      assertEquals(component.component.c, null);
      assertNotNull(value);
      assertEquals(component.a, null);
      assertEquals(component.b, null);
      assertEquals(component.c, null);
      assertFalse(context.getValidation().isValid());

   }
}
