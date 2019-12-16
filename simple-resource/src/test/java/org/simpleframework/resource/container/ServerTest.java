package org.simpleframework.resource.container;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.simpleframework.http.Status;
import org.simpleframework.module.Application;
import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Inject;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.annotation.Provides;
import org.simpleframework.module.annotation.Value;
import org.simpleframework.resource.action.JsonMapper;
import org.simpleframework.resource.action.ResponseEntity;
import org.simpleframework.resource.annotation.Body;
import org.simpleframework.resource.annotation.Consumes;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.POST;
import org.simpleframework.resource.annotation.PUT;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.PathParam;
import org.simpleframework.resource.annotation.Produces;
import org.simpleframework.resource.container.Acceptor;
import org.simpleframework.resource.container.ServerDriver;

import junit.framework.TestCase;

@Module
public class ServerTest extends TestCase {
   
   public void testServerWithVendorContentType() throws Exception {
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
         acceptor.stop();
      }
   }
   
   public void testServerGetWithVendorContentTypePerformance() throws Exception {
      Acceptor acceptor = Application.create(ServerDriver.class)
         .path("..")
         .module(ServerTest.class)
         .create()
         .name("Apache/2.2.14")
         .threads(20)
         .start();
      
      try {
         int iterations = 100000;
         int threads = 20;
         CountDownLatch latch = new CountDownLatch(threads);
         Executor executor = Executors.newFixedThreadPool(threads);
         byte[] chunk = new byte[1024];
         InetSocketAddress address = acceptor.bind();
         int port = address.getPort();
         long start = System.currentTimeMillis();

         for(int j = 0; j < threads; j++) {
            executor.execute(() -> {
               try {
                  for(int i = 0; i < iterations / threads; i++) {
                     URL target = new URL("http://localhost:" + port + "/some/" + i + "/value");
                     HttpURLConnection connection = (HttpURLConnection)target.openConnection();
                     
                     connection.setRequestMethod("GET");
                     connection.setDoOutput(false);
                     connection.setRequestProperty("Accept", "application/vnd.test-v1+json");
                     
                     InputStream stream = connection.getInputStream();                     
                     while(stream.read(chunk) != -1);
                     
                     assertEquals(connection.getResponseCode(), 200);
                     assertEquals(connection.getHeaderField("Server"), "Apache/2.2.14");
                     assertEquals(connection.getHeaderField("Content-Type"), "application/vnd.test-v1+json");
                  }
               } catch(Exception e) {
                  e.printStackTrace();
               } finally {
                  latch.countDown();
               }
            });
         }
         latch.await();
         double duration = (System.currentTimeMillis()-start);
         double timePerRequest = (duration/iterations);
         double requrstsPerSecond = 1000d / timePerRequest;
         
         System.err.println("time="+duration+ " performance="+Math.round(requrstsPerSecond)+ " req/s");
      } finally {
         acceptor.stop();
      }
   }
   
   public void testServerWithDefaultContentType() throws Exception {
      Acceptor acceptor = Application.create(ServerDriver.class)
         .path("..")
         .module(ServerTest.class)
         .create("--message=key", "--text=val")
         .name("Apache/2.2.14")
         .session("SESSIONID")
         .threads(10)
         .start();
      
      try {
         InetSocketAddress address = acceptor.bind();
         int port = address.getPort();
         URL target = new URL("http://localhost:" + port + "/some/123456789/value");
         HttpURLConnection connection = (HttpURLConnection)target.openConnection();
         
         connection.setRequestMethod("GET");
         connection.setDoOutput(false);
         connection.setRequestProperty("Accept", "*");
         
         JsonMapper mapper = new JsonMapper();
         InputStream stream = connection.getInputStream();
         Map map = mapper.readValue(stream, Map.class);
         
         assertEquals(connection.getResponseCode(), 200);
         assertTrue(connection.getHeaderField("Set-Cookie").contains("TEST=123"));
         assertEquals(connection.getHeaderField("Content-Type"), "application/json");
         assertEquals(map.get("key"), "val");
         assertEquals(map.size(), 1);
      } finally {
         acceptor.stop();
      }
   }
   
   public void testServerPostWithDefaultContentType() throws Exception {
      Acceptor acceptor = Application.create(ServerDriver.class)
         .path("..")
         .module(ServerTest.class)
         .create("--message=key", "--text=val")
         .name("Apache/2.2.14")
         .session("SESSIONID")
         .threads(10)
         .start();
      
      try {
         InetSocketAddress address = acceptor.bind();
         int port = address.getPort();
         URL target = new URL("http://localhost:" + port + "/some/123456789/value");
         HttpURLConnection connection = (HttpURLConnection)target.openConnection();
         
         connection.setRequestMethod("POST");
         connection.setDoOutput(true);
         connection.setRequestProperty("Accept", "*");
         connection.setRequestProperty("Content-Type", "application/vnd.test-v1+json");
         connection.getOutputStream().write("{\"id\": \"blah\"}".getBytes());
         
         JsonMapper mapper = new JsonMapper();
         InputStream stream = connection.getInputStream();
         Map map = mapper.readValue(stream, Map.class);
         
         assertEquals(connection.getResponseCode(), 200);
         assertTrue(connection.getHeaderField("Set-Cookie").contains("TEST=123"));
         assertEquals(connection.getHeaderField("Content-Type"), "application/json");
         assertEquals(map.get("id"), "blah");
         assertEquals(map.size(), 1);
      } finally {
         acceptor.stop();
      }
   }
   
   public void testServerPostWithVendorContentType() throws Exception {
      Acceptor acceptor = Application.create(ServerDriver.class)
         .path("..")
         .module(ServerTest.class)
         .create()
         .name("Apache/2.2.14")
         .threads(10)
         .start();
      
      try {
         InetSocketAddress address = acceptor.bind();
         int port = address.getPort();
         URL target = new URL("http://localhost:" + port + "/some/999/value");
         HttpURLConnection connection = (HttpURLConnection)target.openConnection();
         
         connection.setRequestMethod("POST");
         connection.setDoOutput(true);
         connection.setRequestProperty("Accept", "application/vnd.test-v1+json");
         connection.setRequestProperty("Content-Type", "application/vnd.test-v1+json");
         connection.getOutputStream().write("{\"a\": \"b\", \"c\": \"d\"}".getBytes());
         
         JsonMapper mapper = new JsonMapper();
         InputStream stream = connection.getInputStream();
         Map map = mapper.readValue(stream, Map.class);
         
         assertEquals(connection.getResponseCode(), 200);
         assertTrue(connection.getHeaderField("Set-Cookie").contains("TEST=999"));
         assertEquals(connection.getHeaderField("Server"), "Apache/2.2.14");
         assertEquals(connection.getHeaderField("Content-Type"), "application/vnd.test-v1+json");
         assertEquals(map.get("a"), "b");
         assertEquals(map.get("c"), "d");
         assertEquals(map.size(), 2);
      } finally {
         acceptor.stop();
      }
   }
   
   public void testServerPutWithVendorContentType() throws Exception {
      Acceptor acceptor = Application.create(ServerDriver.class)
         .path("..")
         .module(ServerTest.class)
         .create()
         .name("Apache/2.2.14")
         .threads(10)
         .start();
      
      try {
         InetSocketAddress address = acceptor.bind();
         int port = address.getPort();
         URL target = new URL("http://localhost:" + port + "/some/999/value");
         HttpURLConnection connection = (HttpURLConnection)target.openConnection();
         
         connection.setRequestMethod("PUT");
         connection.setDoOutput(true);
         connection.setRequestProperty("Accept", "application/vnd.test-v1+json");
         connection.setRequestProperty("Content-Type", "application/vnd.test-v1+json");
         connection.getOutputStream().write("{\"name\": \"Niall Gallagher\", \"id\": 168}".getBytes());
         
         JsonMapper mapper = new JsonMapper();
         InputStream stream = connection.getInputStream();
         Map map = mapper.readValue(stream, Map.class);
         
         assertEquals(connection.getResponseCode(), 200);
         assertTrue(connection.getHeaderField("Set-Cookie").contains("TEST=999"));
         assertEquals(connection.getHeaderField("Server"), "Apache/2.2.14");
         assertEquals(connection.getHeaderField("Content-Type"), "application/vnd.test-v1+json");
         assertEquals(map.get("name"), "Niall Gallagher");
         assertEquals(map.get("id"), 168);
         assertEquals(map.size(), 2);
      } finally {
         acceptor.stop();
      }
   }
   
   public void testServerPutWithVendorContentTypePerformance() throws Exception {
      Acceptor acceptor = Application.create(ServerDriver.class)
         .path("..")
         .module(ServerTest.class)
         .create()
         .name("Apache/2.2.14")
         .threads(20)
         .start();
      
      try {
         int iterations = 100000;
         int threads = 20;
         CountDownLatch latch = new CountDownLatch(threads);
         Executor executor = Executors.newFixedThreadPool(threads);
         byte[] payload = "{\"name\": \"Niall Gallagher\", \"id\": 168}".getBytes();
         byte[] chunk = new byte[1024];
         InetSocketAddress address = acceptor.bind();
         int port = address.getPort();
         long start = System.currentTimeMillis();

         for(int j = 0; j < threads; j++) {
            executor.execute(() -> {
               try {
                  for(int i = 0; i < iterations / threads; i++) {
                     URL target = new URL("http://localhost:" + port + "/some/" + i + "/value");
                     HttpURLConnection connection = (HttpURLConnection)target.openConnection();
                     
                     connection.setRequestMethod("PUT");
                     connection.setDoOutput(true);
                     connection.setRequestProperty("Accept", "application/vnd.test-v1+json");
                     connection.setRequestProperty("Content-Type", "application/vnd.test-v1+json");
                     connection.getOutputStream().write(payload);
                     
                     InputStream stream = connection.getInputStream();                     
                     while(stream.read(chunk) != -1);
                     
                     assertEquals(connection.getResponseCode(), 200);
                     assertEquals(connection.getHeaderField("Server"), "Apache/2.2.14");
                     assertEquals(connection.getHeaderField("Content-Type"), "application/vnd.test-v1+json");
                  }
               } catch(Exception e) {
                  e.printStackTrace();
               } finally {
                  latch.countDown();
               }
            });
         }
         latch.await();
         double duration = (System.currentTimeMillis()-start);
         double timePerRequest = (duration/iterations);
         double requrstsPerSecond = 1000d / timePerRequest;
         
         System.err.println("time="+duration+ " performance="+Math.round(requrstsPerSecond)+ " req/s");
      } finally {
         acceptor.stop();
      }
   }
   
   @Provides
   public ExampleService service(ExampleComponent component, ExampleClient client, @Value("${message}") String message) {
      return new ExampleService(component, client);
   }
   
   @Provides
   public ExampleClient service(@Value("${message}") String message) {
      return new ExampleClient(message);
   }

   @Component
   public static class ExampleComponent {
      
      @Value("${text}")
      private String text;
      
      
      public String getText() {
         return text;
      }
   }
   
   public static class ExampleClient {
      
      private String message;
      
      public ExampleClient(String message) {
         this.message = message;
      }
   }
   
   public static class ExampleService {
      
      private ExampleComponent component;
      private ExampleClient client;
      
      public ExampleService(ExampleComponent component, ExampleClient client) {
         this.component = component;
         this.client = client;
      }
   }
   
   public static class ExamplePayload {
      
      private String name;
      private int id;
      
      public String getName() {
         return name;
      }
      
      public void setName(String name) {
         this.name = name;
      }
      
      public int getId() {
         return id;
      }
      
      public void setId(int id) {
         this.id = id;
      }
   }

   @Path("/some/{id}")
   @Produces("text/css")
   public static class ExampleResource {
      
      @Inject
      private ExampleService service;
      
      @GET
      @Path("/value")
      @Produces({"application/json", "application/vnd.test-v1+json"})
      public ResponseEntity value(@PathParam("id") String id) {
         return ResponseEntity.create(Status.OK)
            .cookie("TEST", "123")
            .entity(Collections.singletonMap(service.client.message, service.component.text))
            .create();
      }
      
      @POST
      @Path("/value")
      @Consumes({"application/json", "application/vnd.test-v1+json"})
      @Produces({"application/json", "application/vnd.test-v1+json"})
      public ResponseEntity value(@PathParam("id") String id, @Body String body) {
         return ResponseEntity.create(Status.OK)
            .cookie("TEST", id)
            .entity(body)
            .create();
      }
      
      @PUT
      @Path("/value")
      @Consumes("application/vnd.test-v1+json")
      @Produces({"application/json", "application/vnd.test-v1+json"})
      public ResponseEntity value(@PathParam("id") int id, @Body ExamplePayload body) {
         return ResponseEntity.create(Status.OK)
            .cookie("TEST", ""+id)
            .entity(body)
            .create();
      }
   }
}
