package org.simpleframework.module.resource.action.build;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.build.ConstructorScanner;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.ComponentStore;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.resource.action.RequestContextBuilder;
import org.simpleframework.module.resource.annotation.Entity;

import junit.framework.TestCase;

public class ListOfComponentsTest extends TestCase {
   
   @Entity
   public static class SomeComponent implements Serializable {
      
   }
   
   @Entity
   public static class OtherComponent implements Serializable {
      
      private final SomeComponent a;
      private final YetAnotherComponent b;
      
      public OtherComponent(SomeComponent a, YetAnotherComponent b) {
         this.a = a;
         this.b = b;
      }
   }
   
   @Entity
   public static class YetAnotherComponent implements Serializable {
      
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
   
   @Entity
   public static class ListOfSerializable {
      
      private final List<Serializable> list;
      
      public ListOfSerializable(List<Serializable> list) {
         this.list = list;
      }
   }

   public void testDepdencyInjection() throws Exception{
      List<Extractor> extractors = new LinkedList<Extractor>();
      ComponentManager dependencySystem = new ComponentStore();
      ConstructorScanner constructorScanner = new ConstructorScanner(dependencySystem, extractors, ignore -> true);
      List<Function> builders = constructorScanner.createConstructors(SomeOtherComponent.class);
      MockRequest request = new MockRequest("GET", "/?a=A", "");
      MockResponse response = new MockResponse();
      Context context = new RequestContextBuilder().build(request, response);
      
      context.getModel().set("a", "A");
      
      Function builder = builders.iterator().next();
      SomeOtherComponent value = builder.getValue(context);
      
      assertNotNull(value);
      assertNotNull(value.a);
      assertNotNull(value.b);
      assertNotNull(value.b.a);
      assertNotNull(value.b.b);
      assertNotNull(value.b.b.a);
   }
   
   public void testDepdencyInjectionListOfComponents() throws Exception{
      List<Extractor> extractors = new LinkedList<Extractor>();
      ComponentManager dependencySystem = new ComponentStore();
      ComponentFilter filter = new ComponentFilter();
      ConstructorScanner constructorScanner = new ConstructorScanner(dependencySystem, extractors, filter);
      List<Function> builders = constructorScanner.createConstructors(SomeOtherComponent.class);
      List<Function> listBuilders = constructorScanner.createConstructors(ListOfSerializable.class);
      MockRequest request = new MockRequest("GET", "/?a=A", "");
      MockResponse response = new MockResponse();
      Context context = new RequestContextBuilder().build(request, response);
      
      context.getModel().set("a", "A");
      
      Function builder = builders.iterator().next();
      SomeOtherComponent value = builder.getValue(context);
      
      assertNotNull(value);
      assertNotNull(value.a);
      assertNotNull(value.b);
      assertNotNull(value.b.a);
      assertNotNull(value.b.b);
      assertNotNull(value.b.b.a);
      
      Function listBuilder = listBuilders.iterator().next();
      ListOfSerializable listOfSerializable = listBuilder.getValue(context);
      
      assertNotNull(listOfSerializable);
   }
}