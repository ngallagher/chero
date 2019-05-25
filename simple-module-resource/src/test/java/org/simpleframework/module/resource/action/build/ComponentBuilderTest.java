package org.simpleframework.module.resource.action.build;

import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.annotation.Required;
import org.simpleframework.module.build.ConstructorScanner;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.common.ComponentManager;
import org.simpleframework.module.common.DependencyManager;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.Model;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ModelExtractor;
import org.simpleframework.module.resource.action.ActionContextBuilder;
import org.simpleframework.module.resource.action.extract.CookieExtractor;
import org.simpleframework.module.resource.action.extract.HeaderExtractor;
import org.simpleframework.module.resource.action.extract.PartExtractor;
import org.simpleframework.module.resource.action.extract.QueryExtractor;
import org.simpleframework.module.resource.action.extract.RequestExtractor;
import org.simpleframework.module.resource.action.extract.ResponseExtractor;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.annotation.QueryParam;

import junit.framework.TestCase;

public class ComponentBuilderTest extends TestCase {

   public static class SomeComponent {

      @Required
      @QueryParam("a")
      String a;

      @Required
      @QueryParam("b")
      String b;

      @Required
      @QueryParam("int")
      int value;

      @QueryParam("long")
      int num;
      String someFieldWithNoAnnotation = "X";

      public SomeComponent() {
         super();
      }

      public boolean isValid() {
         return a != null && b != null;
      }
   }

   public static class SomeComponentWithImplicitConstructorParams extends SomeComponent {
      Request request;
      Model model;

      public SomeComponentWithImplicitConstructorParams(Model model, Request request) {
         this.model = model;
         this.request = request;
      }

      public boolean isValid() {
         return a != null && b != null;
      }
   }

   public static class SomeComponentWithImplicitAndExplicitConstructorParams extends SomeComponent {
      Request request;
      Model model;
      int someValue;

      public SomeComponentWithImplicitAndExplicitConstructorParams(@Required @Inject("int") int someValue, Model model, Request request) {
         this.model = model;
         this.request = request;
         this.someValue = someValue;
      }

      public boolean isValid() {
         return a != null && b != null;
      }
   }

   public static class SomeComponentWithImplicitAndExplicitConstructorParamsAndOverriddenFields extends SomeComponent {
      @Required
      @Inject
      Request request;
      Response response;
      @Required
      @Inject
      OutputStream output;
      Model model;
      int someValue;
      @QueryParam("a")
      String x;

      public SomeComponentWithImplicitAndExplicitConstructorParamsAndOverriddenFields(@QueryParam("int") int someValue, Model model, Request request) {
         this.model = model;
         this.request = request;
         this.someValue = someValue;
      }

      public boolean isValid() {
         return a != null && b != null;
      }
   }
   
   @Path("/")
   private static class SomeResource {
      
      private final FooService foo;
      private final BlahService blah;
      
      public SomeResource(FooService foo, BlahService blah) {
         this.foo = foo;
         this.blah = blah;
      }
   }
   
   private static class FooService {
      
      private final String name;
      
      public FooService(String name) {
         this.name = name;
      }
   }

   private static class BlahService {
      
      private final String name;
      
      public BlahService(String name) {
         this.name = name;
      }
   }
   
   private ConstructorScanner scanner;

   public void setUp() {
      List<Extractor> extractors = new LinkedList<Extractor>();
      
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      
      DependencyManager system = new ComponentManager();
      
      system.register(new FooService("foo"));
      system.register(new BlahService("blah1"));
      
      scanner = new ConstructorScanner(system, extractors, argument -> false);
   }
   
   public void testComponentBuilderWithConstructorInjection() throws Exception {
      List<Function> builders = scanner.createConstructors(SomeResource.class);
      MockRequest request = new MockRequest("GET", "/?a=A&b=B&int=5", "");
      MockResponse response = new MockResponse();
      Context context = new ActionContextBuilder().build(request, response);
      Function builder = builders.iterator().next();
      Object value = builder.getValue(context);
      SomeResource component = (SomeResource) value;

      assertNotNull(value);
      assertNotNull(component.foo);
      assertEquals(component.foo.getClass(), FooService.class);
      assertNotNull(component.blah);
      assertEquals(component.blah.getClass(), BlahService.class);
   }
   

   public void testComponentBuilderWithImplicitParams() throws Exception {
      List<Function> builders = scanner.createConstructors(SomeComponentWithImplicitConstructorParams.class);
      MockRequest request = new MockRequest("GET", "/?a=A&b=B&int=5", "");
      MockResponse response = new MockResponse();
      Context context = new ActionContextBuilder().build(request, response);
      Function builder = builders.iterator().next();
      Object value = builder.getValue(context);
      SomeComponentWithImplicitConstructorParams component = (SomeComponentWithImplicitConstructorParams) value;

      assertNotNull(value);
      assertEquals(component.a, "A");
      assertEquals(component.b, "B");
      assertEquals(component.value, 5);
      assertEquals(component.someFieldWithNoAnnotation, "X");
      //assertEquals(component.request, request);
      assertEquals(component.model, context.getModel());
   }

   public void testComponentBuilderWithImplicitAndExplicitParams() throws Exception {
      List<Function> builders = scanner.createConstructors(SomeComponentWithImplicitAndExplicitConstructorParamsAndOverriddenFields.class);
      MockRequest request = new MockRequest("GET", "/?a=A&b=B&int=5", "");
      MockResponse response = new MockResponse(System.err);
      Context context = new ActionContextBuilder().build(request, response);
      Function builder = builders.iterator().next();
      Object value = builder.getValue(context);
      SomeComponentWithImplicitAndExplicitConstructorParamsAndOverriddenFields component = (SomeComponentWithImplicitAndExplicitConstructorParamsAndOverriddenFields) value;

      assertNotNull(value);
      assertEquals(component.a, "A");
      assertEquals(component.b, "B");
      assertEquals(component.value, 5);
      assertEquals(component.someFieldWithNoAnnotation, "X");
      assertNull(component.response);
      assertEquals(component.output, System.err);
      assertEquals(component.request, request);
      assertEquals(component.model, context.getModel());
      assertEquals(component.someValue, 5);
      assertEquals(component.x, "A");
   }
}
