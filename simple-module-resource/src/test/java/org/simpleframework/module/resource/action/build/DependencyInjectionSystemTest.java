package org.simpleframework.module.resource.action.build;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.build.ConstructorScanner;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.common.ComponentManager;
import org.simpleframework.module.common.DependencyManager;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ModelExtractor;
import org.simpleframework.module.resource.action.ActionContextBuilder;
import org.simpleframework.module.resource.action.extract.CookieExtractor;
import org.simpleframework.module.resource.action.extract.HeaderExtractor;
import org.simpleframework.module.resource.action.extract.PartExtractor;
import org.simpleframework.module.resource.action.extract.QueryExtractor;
import org.simpleframework.module.resource.action.extract.RequestExtractor;
import org.simpleframework.module.resource.action.extract.ResponseExtractor;
import org.simpleframework.module.resource.annotation.Entity;

import junit.framework.TestCase;

public class DependencyInjectionSystemTest extends TestCase {
   
   @Entity
   public static class SomeComponent {
      
   }
   
   @Entity
   public static class OtherComponent {
      
      private final SomeComponent a;
      private final YetAnotherComponent b;
      
      public OtherComponent(SomeComponent a, YetAnotherComponent b) {
         this.a = a;
         this.b = b;
      }
   }
   
   @Entity
   public static class YetAnotherComponent {
      
      private SomeComponent a;
   
      public YetAnotherComponent(SomeComponent a) {
         this.a = a;
      }
   }
   
   @Entity
   public static class SomeOtherComponent {
      
      private final SomeComponent a;
      private final OtherComponent b;
      
      public SomeOtherComponent(SomeComponent a, OtherComponent b) {
         this.a = a;
         this.b = b;
      }
   }

   public void testDepdencyInjection() throws Exception{
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());

      DependencyManager dependencySystem = new ComponentManager();
      ComponentFilter filter = new ComponentFilter();
      ConstructorScanner constructorScanner = new ConstructorScanner(dependencySystem, extractors, filter);
      
      List<Function> builders = constructorScanner.createConstructors(SomeOtherComponent.class);
      MockRequest request = new MockRequest("GET", "/?a=A", "");
      MockResponse response = new MockResponse();
      Context context = new ActionContextBuilder().build(request, response);
      Function builder = builders.iterator().next();
      SomeOtherComponent value = builder.getValue(context);
      
      assertNotNull(value);
      assertNotNull(value.a);
      assertNotNull(value.b);
      assertNotNull(value.b.a);
      assertNotNull(value.b.b);
      assertNotNull(value.b.b.a);
   }
}
