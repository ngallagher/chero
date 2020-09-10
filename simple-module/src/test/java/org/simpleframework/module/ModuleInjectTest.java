package org.simpleframework.module;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.annotation.Value;
import org.simpleframework.module.service.Service;
import org.simpleframework.module.service.ServiceDriver;

import junit.framework.TestCase;

public class ModuleInjectTest extends TestCase {
   
   @SuppressWarnings("unchecked")
   public void testModuleInject() {
      Service<Service<?>> service = Application.create(ServiceDriver.class)
         .register(ExampleApplication.class)
         .create("--message=hi");
      
      service.start();
      
      ExampleApplication application = service.resolve(ExampleApplication.class);
      ExampleService object = service.resolve(ExampleService.class);
      ExampleOther other = service.resolve(ExampleOther.class);
      ExampleWrapper wrapper = service.resolve(ExampleWrapper.class);
      
      assertNotNull(application);
      assertNotNull(object);
      assertNotNull(other);
      assertNotNull(other.service);
      assertEquals(other.service, object);
      assertNotNull(wrapper);
      assertNotNull(wrapper.service);
      assertEquals(wrapper.service, object);
      assertEquals(object.message(), "hi");
   }

   @Module
   public static class ExampleApplication {
      
      @Provides
      public ExampleService service(ExampleBlah blah, @Value("${message}") String text) {
         return new ExampleService(text);
      }
      
      @Provides
      public ExampleWrapper service(ExampleService blah) {
         return new ExampleWrapper(blah);
      }
   }
   
   public static class ExampleWrapper {
      
      private ExampleService service;
      
      public ExampleWrapper(ExampleService service) {
         this.service = service;
      }
   }
   
   @Component
   public static class ExampleBlah {
   }
   
   @Component
   public static class ExampleOther {
      
      private ExampleService service;
      
      public ExampleOther(ExampleService service) {
         this.service = service;
      }
   }
   
   public static class ExampleService {
      
      private String text;
      
      public ExampleService(String text) {
         this.text = text;
      }
      
      public String message() {
         return text;
      }
   }
}
