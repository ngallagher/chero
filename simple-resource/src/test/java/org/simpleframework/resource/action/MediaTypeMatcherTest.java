package org.simpleframework.resource.action;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.http.Protocol;
import org.simpleframework.resource.MediaTypeMatcher;
import org.simpleframework.resource.build.MockRequest;

import junit.framework.TestCase;

public class MediaTypeMatcherTest extends TestCase {

   public void testContentTypeMatch() {
      MockRequest request = new MockRequest("GET", "/", "{\"id\": 11}");
      List<String> produces = new ArrayList<String>();
      MediaTypeMatcher matcher = new MediaTypeMatcher(produces, Protocol.ACCEPT);
      
      produces.add("application/json");
      produces.add("application/xml");
      produces.add("application/vnd.exchange-v1+json");
      
      request.setValue(Protocol.ACCEPT, "application/*, */*");
      assertEquals("application/json", matcher.match(request));
      
      request.setValue(Protocol.ACCEPT, "application/xml, */*");
      assertEquals("application/xml", matcher.match(request));
      
      request.setValue(Protocol.ACCEPT, "*/*");
      assertEquals("application/json", matcher.match(request));
      
      request.setValue(Protocol.ACCEPT, "*/xml");
      assertEquals("application/xml", matcher.match(request));
      
      request.setValue(Protocol.ACCEPT, "application/vnd.exchange-v1+json");
      assertEquals("application/vnd.exchange-v1+json", matcher.match(request));
      
      request.setValue(Protocol.ACCEPT, "application/vnd.exchange-v2+json");
      assertNull(matcher.match(request));
      
      request.setValue(Protocol.ACCEPT, "*");
      assertEquals("application/json", matcher.match(request));
   }
}
