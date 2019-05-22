package org.simpleframework.module.resource.action.extract;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.resource.action.build.MockRequest;
import org.simpleframework.module.resource.action.build.MockResponse;
import org.simpleframework.service.ComponentManager;
import org.simpleframework.service.DependencyManager;
import org.simpleframework.service.build.ComponentBuilder;
import org.simpleframework.service.build.extract.ComponentExtractor;
import org.simpleframework.service.build.extract.Extractor;
import org.simpleframework.service.build.extract.ModelExtractor;
import org.simpleframework.service.build.extract.Parameter;
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
import org.simpleframework.service.resource.annotation.QueryParam;

import junit.framework.TestCase;

public class NoArgComponentExtractorTest extends TestCase {

   @Payload
   public static class Query {

      public final String x;
      public final String y;

      public Query() {
         this("defaultX");
      }

      public Query(@QueryParam("x") String x) {
         this(x, "defaultY");
      }

      public Query(@QueryParam("x") String x, @QueryParam("y") String y) {
         this.x = x;
         this.y = y;
      }
   }

   public void testComponentExtractor() throws Exception {
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
      List<ComponentBuilder> builder = scanner.createBuilders(Query.class);
      ComponentExtractor extractor = new ComponentExtractor(builder, Query.class);
      Parameter parameter = new Parameter(Query.class, null, null, Collections.EMPTY_MAP, false);
      MockRequest request = new MockRequest("GET", "/?x=X", "");
      MockResponse response = new MockResponse();
      Context context = new ActionContextBuilder().build(request, response);
      Query query = (Query) extractor.extract(parameter, context);

      assertEquals(query.x, "X");
      assertEquals(query.y, "defaultY");

      request = new MockRequest("GET", "/", "");
      response = new MockResponse();
      context = new ActionContextBuilder().build(request, response);
      query = (Query) extractor.extract(parameter, context);

      assertEquals(query.x, "defaultX");
      assertEquals(query.y, "defaultY");

      request = new MockRequest("GET", "/?y=Y", "");
      response = new MockResponse();
      context = new ActionContextBuilder().build(request, response);
      query = (Query) extractor.extract(parameter, context);

      assertEquals(query.x, null);
      assertEquals(query.y, "Y");
   }

}
