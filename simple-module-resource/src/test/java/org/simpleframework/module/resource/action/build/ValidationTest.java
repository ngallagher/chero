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
import org.simpleframework.module.resource.action.ActionContextBuilder;
import org.simpleframework.module.resource.action.extract.CookieExtractor;
import org.simpleframework.module.resource.action.extract.HeaderExtractor;
import org.simpleframework.module.resource.action.extract.PartExtractor;
import org.simpleframework.module.resource.action.extract.QueryExtractor;
import org.simpleframework.module.resource.action.extract.RequestExtractor;
import org.simpleframework.module.resource.action.extract.ResponseExtractor;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.annotation.Entity;
import org.simpleframework.module.resource.annotation.QueryParam;

import junit.framework.TestCase;

public class ValidationTest extends TestCase {

   @Entity
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

   @Entity
   public static class InvalidComponent {

      @Required
      @QueryParam("a")
      public String x;

      @Required
      @QueryParam("b")
      public String y;

   }

   @Path("/somePath")
   public static class ControllerThatTakesComponent {

      @Path
      public void updateComponent(InvalidComponent component, Model model) {
         model.set("x", "x");
      }

   }

   public void testControllerWithValidation() throws Throwable {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      ComponentManager dependencySystem = new ComponentStore();
      ClassFinder finder = new ClassFinder(ControllerThatTakesComponent.class);
      Validator validator = new AnnotationValidator();
      ComponentFilter filter = new ComponentFilter();
      ConstructorScanner constructorScanner = new ConstructorScanner(dependencySystem, extractors, filter);
      MethodScanner methodScanner = new MethodScanner(dependencySystem, constructorScanner, extractors, filter);
      ActionScanner scanner = new ActionScanner(methodScanner, validator);
      MethodDispatcherResolver resolver = new MethodDispatcherResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/some-path/update-component?a=niall.gallagher@rbs.com&enum=X", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new ActionContextBuilder().build(request, response);
      MethodDispatcher dispatcher = resolver.resolveBest(context);
      dispatcher.execute(context);

      assertFalse(context.getValidation().isValid());
      assertEquals(context.getValidation().getErrors().size(), 1);
   }
}
