package org.simpleframework.module.resource.action.build;

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
import org.simpleframework.module.core.MapContext;
import org.simpleframework.module.core.Validator;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.extract.ModelExtractor;
import org.simpleframework.module.resource.action.RequestContextBuilder;
import org.simpleframework.module.resource.action.build.ActionScanner;
import org.simpleframework.module.resource.action.build.MethodDispatcher;
import org.simpleframework.module.resource.action.build.MethodDispatcherResolver;
import org.simpleframework.module.resource.action.extract.CookieExtractor;
import org.simpleframework.module.resource.action.extract.HeaderExtractor;
import org.simpleframework.module.resource.action.extract.PartExtractor;
import org.simpleframework.module.resource.action.extract.QueryExtractor;
import org.simpleframework.module.resource.action.extract.RequestExtractor;
import org.simpleframework.module.resource.action.extract.ResponseExtractor;
import org.simpleframework.module.resource.annotation.Attachment;
import org.simpleframework.module.resource.annotation.Path;

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
      MethodDispatcherResolver resolver = new MethodDispatcherResolver(scanner, finder);
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

