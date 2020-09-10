package org.simpleframework.module.split;

import org.simpleframework.module.Application;
import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.annotation.Value;
import org.simpleframework.module.service.Service;
import org.simpleframework.module.service.ServiceDriver;

import junit.framework.TestCase;

public class SameTypeDifferentProvidersTest extends TestCase {
   
   @SuppressWarnings("unchecked")
   public void testModuleInject() {
      Service<Service<?>> service = Application.create(ServiceDriver.class)
         .path("..")
         .register(ExampleApp.class)
         .create("--message=hi");
      
      service.start();
      
      ExampleAggregate aggregate = service.resolve(ExampleAggregate.class);
      aggregate.test();
   }
   
   @Module
   public static class ExampleApp {
      
   }
   
   @Module
   public static class ExampleMod {
      
      @Provides("foo")
      public ExampleService serviceA(@Value("${message}") String text) {
         return new ExampleService("foo");
      }
      
      @Provides
      public ExampleService serviceB(@Value("${message}") String text) {
         return new ExampleService("anonymous");
      }
   }
   
   @Module
   public static class ExampleOtherMod {
      
      @Provides("blah")
      public ExampleService service() {
         return new ExampleService("blah");
      }
   }
   
   public static class ExampleService {
      
      private final String name;
      
      public ExampleService(String name) {
         this.name = name;
      }
      
      public String getName() {
         return name;
      }
   }
   
   @Component
   public static class ExampleAggregate {
      
      private final ExampleService x;
      private final ExampleService y;
      
      public ExampleAggregate(
            @Inject("foo") ExampleService x,
            @Inject("blah") ExampleService y)
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
