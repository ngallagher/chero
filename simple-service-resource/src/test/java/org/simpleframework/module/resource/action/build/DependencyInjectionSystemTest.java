package org.simpleframework.module.resource.action.build;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.service.ComponentManager;
import org.simpleframework.service.DependencyManager;
import org.simpleframework.service.annotation.Component;
import org.simpleframework.service.build.ComponentBuilder;
import org.simpleframework.service.build.DependencyScanner;
import org.simpleframework.service.build.extract.Extractor;
import org.simpleframework.service.build.extract.ModelExtractor;
import org.simpleframework.service.context.Context;
import org.simpleframework.service.resource.action.ActionContextBuilder;
import org.simpleframework.service.resource.action.build.ActionScanner;
import org.simpleframework.service.resource.action.extract.CookieExtractor;
import org.simpleframework.service.resource.action.extract.HeaderExtractor;
import org.simpleframework.service.resource.action.extract.PartExtractor;
import org.simpleframework.service.resource.action.extract.QueryExtractor;
import org.simpleframework.service.resource.action.extract.RequestExtractor;
import org.simpleframework.service.resource.action.extract.ResponseExtractor;
import org.simpleframework.service.resource.annotation.Payload;

import junit.framework.TestCase;

public class DependencyInjectionSystemTest extends TestCase {
   
   @Payload
   public static class SomeComponent {
      
   }
   
   @Payload
   public static class OtherComponent {
      
      private final SomeComponent a;
      private final YetAnotherComponent b;
      
      public OtherComponent(SomeComponent a, YetAnotherComponent b) {
         this.a = a;
         this.b = b;
      }
   }
   
   @Payload
   public static class YetAnotherComponent {
      
      private SomeComponent a;
   
      public YetAnotherComponent(SomeComponent a) {
         this.a = a;
      }
   }
   
   @Payload
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
      ActionScanner scanner = new ActionScanner(dependencySystem, extractors);
      List<ComponentBuilder> builders = scanner.createBuilders(SomeOtherComponent.class);
      MockRequest request = new MockRequest("GET", "/?a=A", "");
      MockResponse response = new MockResponse();
      Context context = new ActionContextBuilder().build(request, response);
      ComponentBuilder builder = builders.iterator().next();
      SomeOtherComponent value = builder.build(context);
      
      assertNotNull(value);
      assertNotNull(value.a);
      assertNotNull(value.b);
      assertNotNull(value.b.a);
      assertNotNull(value.b.b);
      assertNotNull(value.b.b.a);
   }
}
