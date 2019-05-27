package org.simpleframework.module.resource.action.build;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.annotation.Required;
import org.simpleframework.module.build.ConstructorScanner;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.ComponentStore;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ModelExtractor;
import org.simpleframework.module.resource.action.ActionContextBuilder;
import org.simpleframework.module.resource.action.extract.CookieExtractor;
import org.simpleframework.module.resource.action.extract.HeaderExtractor;
import org.simpleframework.module.resource.action.extract.PartExtractor;
import org.simpleframework.module.resource.action.extract.QueryExtractor;
import org.simpleframework.module.resource.action.extract.RequestExtractor;
import org.simpleframework.module.resource.action.extract.ResponseExtractor;
import org.simpleframework.module.resource.annotation.QueryParam;

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

   private ComponentManager dependencySystem;
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
      
      dependencySystem = new ComponentStore();
      scanner = new ConstructorScanner(dependencySystem, extractors, argument -> false);
   }
   
   public void testTest() {}
   
//   public void testBuilder() throws Exception {
//      List<Function> builders = scanner.createConstructors(SomeComponent.class);
//      MockRequest request = new MockRequest("GET", "/?a=A", "");
//      MockResponse response = new MockResponse();
//      Context context = new ActionContextBuilder().build(request, response);
//      Function builder = builders.iterator().next();
//      Object value = builder.getValue(context);
//      SomeComponent component = (SomeComponent) value;
//
//      System.err.println(context.getValidation().getErrors());
//
//      assertNotNull(value);
//      assertEquals(component.a, "A");
//      assertEquals(component.b, null);
//      assertEquals(component.c, null);
//      assertFalse(context.getValidation().isValid());
//      assertEquals(context.getValidation().getErrors().size(), 1);
//   }
//
//   public void testBuilderWithNoParametersAtAll() throws Exception {
//      List<Function> builders = scanner.createConstructors(SomeComponentWithSomeComponent.class);
//      MockRequest request = new MockRequest("GET", "/", "");
//      MockResponse response = new MockResponse();
//      Context context = new ActionContextBuilder().build(request, response);
//      Function builder = builders.iterator().next();
//      Object value = builder.getValue(context);
//      SomeComponentWithSomeComponent component = (SomeComponentWithSomeComponent) value;
//
//      System.err.println(context.getValidation().getErrors());
//
//      assertNotNull(component.component);
//      assertEquals(component.component.a, null);
//      assertEquals(component.component.b, null);
//      assertEquals(component.component.c, null);
//      assertNotNull(value);
//      assertEquals(component.a, null);
//      assertEquals(component.b, null);
//      assertEquals(component.c, null);
//      assertFalse(context.getValidation().isValid());
//
//   }
}
