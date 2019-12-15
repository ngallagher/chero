package org.simpleframework.module.provides;

import org.simpleframework.module.Application;
import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.annotation.Value;
import org.simpleframework.module.service.Service;
import org.simpleframework.module.service.ServiceDriver;

import junit.framework.TestCase;

public class NamedProviderTest extends TestCase {

   @SuppressWarnings("unchecked")
   public void testModuleInject() {
      Service<Service<?>> service = Application.create(ServiceDriver.class)
         .path("..")
         .module(SimpleApp.class)
         .create("--message=hi");
      
      service.start();
      
      SimpleAggregate aggregate = service.resolve(SimpleAggregate.class);
      aggregate.test();
   }
   
   @Module
   public static class SimpleApp {
      
   }
   
   @Module
   public static class SimpleMod {
      
      @Provides("foo")
      public SimpleService serviceA(@Value("${message}") String text) {
         return new SimpleService("foo");
      }
      
      @Provides("blah")
      public SimpleService service() {
         return new SimpleService("blah");
      }
   }
   
   public static class SimpleService {
      
      private final String name;
      
      public SimpleService(String name) {
         this.name = name;
      }
      
      public String getName() {
         return name;
      }
   }
   
   @Component
   public static class SimpleAggregate {
      
      private final SimpleService x;
      private final SimpleService y;
      
      public SimpleAggregate(
            @Inject("foo") SimpleService x,
            @Inject("blah") SimpleService y)
      {
         this.x = x;
         this.y = y;
      }
      
      public void test() {
         if(!x.name.equals("foo")) {
            throw new IllegalStateException("Wrong value for foo");
         }
         if(!y.name.equals("blah")) {
            throw new IllegalStateException("Wrong value for blah");
         }
      }
   }
}
