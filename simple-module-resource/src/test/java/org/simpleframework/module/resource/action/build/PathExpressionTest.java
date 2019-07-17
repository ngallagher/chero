package org.simpleframework.module.resource.action.build;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.module.build.Declaration;
import org.simpleframework.module.build.Parameter;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.PathParam;

import junit.framework.TestCase;

public class PathExpressionTest extends TestCase {

   
   private static Parameter createParameterOf(Class type, String name) {
      Map<Class, Annotation> annotations = new HashMap<Class, Annotation>();
      Declaration declaration = new Declaration(annotations, type, new Class[] {}, null, true);
      Parameter parameter = new Parameter(null, declaration, new Class[] {}, 0);
      
      annotations.put(PathParam.class, new PathParam() {

         @Override
         public Class<? extends Annotation> annotationType() {
            return PathParam.class;
         }

         @Override
         public String value() {
            return name;
         }
      });
      
      return parameter;
   }
   
   public void testPatternForInteger() throws Exception {
      Parameter integer = createParameterOf(int.class, "id");
      PathPattern path = new PathPattern(null, "/", "/path/other/{id}") ;
      MethodMatcher parser = new MethodMatcher(GET.class, path, integer);
      Map<String, String> parameters = parser.evaluate("/path/other/10");

      assertFalse(parameters.isEmpty());
      assertEquals(parameters.get("id"), "10");
      assertEquals(parser.pattern(), "/path/other/([0-9]+)");
   }
   
   public void testPatternForIntegerAndDouble() throws Exception {
      Parameter integer = createParameterOf(int.class, "id");
      Parameter decimal = createParameterOf(Float.class, "factor");
      PathPattern path = new PathPattern(null, "/", "/path/other/{id}/top/{factor}");
      MethodMatcher parser = new MethodMatcher(GET.class, path, integer, decimal);
      Map<String, String> parameters = parser.evaluate("/path/other/10/top/11.0");

      assertFalse(parameters.isEmpty());
      assertEquals(parameters.get("id"), "10");
      assertEquals(parameters.get("factor"), "11.0");
      assertEquals(parser.pattern(), "/path/other/([0-9]+)/top/([0-9\\.]+)");
   }
   
   public void testSlash() throws Exception {
      PathPattern path = new PathPattern(null, "/");
      MethodMatcher parser = new MethodMatcher(GET.class, path);
      Map<String, String> parameters = parser.evaluate("/path/1/path.2");

      assertTrue(parameters.isEmpty());
   }

   public void testSimpleExpressions() throws Exception {
      PathPattern path = new PathPattern(null, "/{param}/.*");
      MethodMatcher parser = new MethodMatcher(GET.class, path);
      Map<String, String> parameters = parser.evaluate("/path/1/path.2");

      assertEquals(parameters.get("param"), "path");

   }

   public void testExpressions() throws Exception {
      PathPattern path = new PathPattern(null, "/path/{param1}/path.{param2}");
      MethodMatcher parser = new MethodMatcher(GET.class, path);
      Map<String, String> parameters = parser.evaluate("/path/1/path.2");

      assertEquals(parameters.get("param1"), "1");
      assertEquals(parameters.get("param2"), "2");

   }

   public void testMultipleParts() throws Exception {
      PathPattern path = new PathPattern(null, "/rootPath/{x}", "/path/{param1}/path.{param2}");
      MethodMatcher parser = new MethodMatcher(GET.class, path);
      Map<String, String> parameters = parser.evaluate("/rootPath/test/path/1/path.2");

      assertEquals(parameters.get("x"), "test");
      assertEquals(parameters.get("param1"), "1");
      assertEquals(parameters.get("param2"), "2");

   }

   public void testMultiplePartsThatMayNotConnectWell() throws Exception {
      PathPattern path = new PathPattern(null, "/rootPath/{x}/", "path/{param1}/path.{param2}");
      MethodMatcher parser = new MethodMatcher(GET.class, path);
      Map<String, String> parameters = parser.evaluate("/rootPath/test/path/1/path.2");

      assertEquals(parameters.get("x"), "test");
      assertEquals(parameters.get("param1"), "1");
      assertEquals(parameters.get("param2"), "2");
   }

   public void testMoreMultiplePartsThatMayNotConnectWell() throws Exception {
      PathPattern path = new PathPattern(null, "/rootPath/{x}", "path/{param1}/path.{param2}");
      MethodMatcher parser = new MethodMatcher(GET.class, path);
      Map<String, String> parameters = parser.evaluate("/rootPath/test/path/1/path.2");

      assertEquals(parameters.get("x"), "test");
      assertEquals(parameters.get("param1"), "1");
      assertEquals(parameters.get("param2"), "2");

   }
}
