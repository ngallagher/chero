package org.simpleframework.module.resource.container;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.simpleframework.http.Status;
import org.simpleframework.module.Application;
import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.annotation.Value;
import org.simpleframework.module.resource.action.JsonMapper;
import org.simpleframework.module.resource.action.ResponseEntity;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.annotation.PathParam;
import org.simpleframework.module.resource.annotation.Produces;

import junit.framework.TestCase;

@Module
public class ServerTest extends TestCase {
   
   public void testServer() throws Exception {
      Acceptor acceptor = Application.create(ServerDriver.class)
         .path("..")
         .module(ServerTest.class)
         .create("--message=bar", "--text=foo")
         .name("Apache/2.2.14")
         .session("SESSIONID")
         .threads(10)
         .start();
      
      try {
         InetSocketAddress address = acceptor.bind();
         int port = address.getPort();
         URL target = new URL("http://localhost:" + port + "/some/11/value");
         HttpURLConnection connection = (HttpURLConnection)target.openConnection();
         
         connection.setRequestMethod("GET");
         connection.setDoOutput(false);
         connection.setRequestProperty("Accept", "application/vnd.test-v1+json");
         
         JsonMapper mapper = new JsonMapper();
         InputStream stream = connection.getInputStream();
         Map map = mapper.readValue(stream, Map.class);
         
         assertEquals(connection.getResponseCode(), 200);
         assertTrue(connection.getHeaderField("Set-Cookie").contains("TEST=123"));
         assertEquals(connection.getHeaderField("Content-Type"), "application/vnd.test-v1+json");
         assertEquals(map.get("bar"), "foo");
         assertEquals(map.size(), 1);
      } finally {
         acceptor.close();
      }
   }
   
   @Provides
   public ExampleService service(ExampleComponent component, @Value("${message}") String message) {
      return new ExampleService(component, message);
   }

   @Component
   public static class ExampleComponent {
      
      @Value("${text}")
      private String text;
      
      
      public String getText() {
         return text;
      }
   }
   
   public static class ExampleService {
      
      private ExampleComponent component;
      private String message;
      
      public ExampleService(ExampleComponent component, String message) {
         this.component = component;
         this.message = message;
      }
   }

   @Path("/some/{id}")
   @Produces("text/css")
   public static class DemoResource {
      
      @Inject
      private ExampleService service;
      
      @GET
      @Path("/value")
      @Produces({"application/json", "application/vnd.test-v1+json"})
      public ResponseEntity value(@PathParam("id") String id) {
         return ResponseEntity.create(Status.OK)
            .cookie("TEST", "123")
            .entity(Collections.singletonMap(service.message, service.component.text))
            .create();
      }
   }
}
