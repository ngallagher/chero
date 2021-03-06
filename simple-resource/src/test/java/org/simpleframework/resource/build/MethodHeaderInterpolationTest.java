package org.simpleframework.resource.build;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.http.Response;
import org.simpleframework.module.build.ConstructorScanner;
import org.simpleframework.module.build.MethodScanner;
import org.simpleframework.module.core.AnnotationValidator;
import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.core.ComponentStore;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Validator;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ModelExtractor;
import org.simpleframework.resource.action.RequestContextBuilder;
import org.simpleframework.resource.annotation.Attachment;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.build.ActionScanner;
import org.simpleframework.resource.build.ClassFinder;
import org.simpleframework.resource.build.ComponentFilter;
import org.simpleframework.resource.build.MethodDispatcher;
import org.simpleframework.resource.build.MethodDispatcherResolver;
import org.simpleframework.resource.build.MethodMatchIndexer;
import org.simpleframework.resource.extract.CookieExtractor;
import org.simpleframework.resource.extract.HeaderExtractor;
import org.simpleframework.resource.extract.PartExtractor;
import org.simpleframework.resource.extract.QueryExtractor;
import org.simpleframework.resource.extract.RequestExtractor;
import org.simpleframework.resource.extract.ResponseExtractor;

import junit.framework.TestCase;

public class MethodHeaderInterpolationTest extends TestCase {
   
   @Path
   public static class ExampleObject {      
      
      @Path("/export")
      @Attachment("${token}.xls")
      public void exportSomeSpreadSheet(Response response) throws IOException {
        PrintStream out = response.getPrintStream();
        out.println("col1,col2,col3");
        out.println("1,2,3");
        out.println("a,b,c");
        out.close();
      }      
   }
   
   public void testInterpolatedHeader() throws Exception {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      ComponentManager dependencySystem = new ComponentStore();
      ClassFinder finder = new ClassFinder(ExampleObject.class);
      Validator validator = new AnnotationValidator();
      ComponentFilter filter = new ComponentFilter();
      ConstructorScanner constructorScanner = new ConstructorScanner(dependencySystem, extractors, filter);
      MethodScanner methodScanner = new MethodScanner(dependencySystem, constructorScanner, extractors, filter);
      ActionScanner scanner = new ActionScanner(methodScanner, validator);
      
      MethodMatchIndexer indexer = new MethodMatchIndexer(scanner, finder);
      MethodDispatcherResolver resolver = new MethodDispatcherResolver(indexer);
      MockRequest request = new MockRequest("GET", "/export?token=reportSpreadSheet", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new RequestContextBuilder().build(request, response);
      
      context.getModel().set("token", "reportSpreadSheet");
      
      MethodDispatcher dispatcher = resolver.resolveBest(context);      
      dispatcher.execute(context);
      
      String header = response.getValue("Content-Disposition");      
      assertTrue(header.indexOf("reportSpreadSheet.xls") != -1);
   }

}

