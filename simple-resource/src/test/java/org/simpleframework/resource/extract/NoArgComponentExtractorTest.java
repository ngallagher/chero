package org.simpleframework.resource.extract;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.build.ConstructorScanner;
import org.simpleframework.module.build.Declaration;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.build.Parameter;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.ComponentStore;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.extract.ComponentExtractor;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ModelExtractor;
import org.simpleframework.resource.action.RequestContextBuilder;
import org.simpleframework.resource.annotation.Entity;
import org.simpleframework.resource.annotation.QueryParam;
import org.simpleframework.resource.build.ComponentFilter;
import org.simpleframework.resource.build.MockRequest;
import org.simpleframework.resource.build.MockResponse;
import org.simpleframework.resource.extract.CookieExtractor;
import org.simpleframework.resource.extract.HeaderExtractor;
import org.simpleframework.resource.extract.PartExtractor;
import org.simpleframework.resource.extract.QueryExtractor;
import org.simpleframework.resource.extract.RequestExtractor;
import org.simpleframework.resource.extract.ResponseExtractor;

import junit.framework.TestCase;

public class NoArgComponentExtractorTest extends TestCase {

   @Entity
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
      ComponentManager dependencySystem = new ComponentStore();
      ComponentFilter filter = new ComponentFilter();
      ConstructorScanner constructorScanner = new ConstructorScanner(dependencySystem, extractors, filter);
      List<Function> builder = constructorScanner.createConstructors(Query.class);
      ComponentExtractor extractor = new ComponentExtractor(builder, Query.class);
      Declaration declaration = new Declaration(Collections.EMPTY_MAP, Query.class, new Class[] {}, null, false);
      Parameter parameter = new Parameter(null, declaration, new Class[] {}, 0);
      MockRequest request = new MockRequest("GET", "/?x=X", "");
      MockResponse response = new MockResponse();
      Context context = new RequestContextBuilder().build(request, response);
      Query query = (Query) extractor.extract(parameter, context);

      assertEquals(query.x, "X");
      assertEquals(query.y, "defaultY");

      request = new MockRequest("GET", "/", "");
      response = new MockResponse();
      context = new RequestContextBuilder().build(request, response);
      query = (Query) extractor.extract(parameter, context);

      assertEquals(query.x, "defaultX");
      assertEquals(query.y, "defaultY");

//      request = new MockRequest("GET", "/?y=Y", "");
//      response = new MockResponse();
//      context = new ActionContextBuilder().build(request, response);
//      query = (Query) extractor.extract(parameter, context);
//
//      assertEquals(query.x, null);
//      assertEquals(query.y, "Y");
   }

}
