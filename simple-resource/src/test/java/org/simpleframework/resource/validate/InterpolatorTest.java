package org.simpleframework.resource.validate;

import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Interpolator;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.core.TokenFilter;
import org.simpleframework.module.core.ContextFilter;
import org.simpleframework.resource.action.RequestContextBuilder;
import org.simpleframework.resource.build.MockRequest;
import org.simpleframework.resource.build.MockResponse;

import junit.framework.TestCase;

public class InterpolatorTest extends TestCase {

   public void testInterpolation() throws Exception {
      MockRequest request = new MockRequest("GET", "/a/b/c/showA?x=X&y=Y", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new RequestContextBuilder().build(request, response);
      TokenFilter filter = new ContextFilter(context);
      Interpolator interpolator = new Interpolator(filter);

      Model model = context.getModel();
      model.set("x", "X");
      model.set("y", "Y");
      model.set("z", "Z");
      
      assertEquals(interpolator.interpolate("The value of y is '${y}'"), "The value of y is 'Y'");
      assertEquals(interpolator.interpolate("x=${x} y=${y}"), "x=X y=Y");
      assertEquals(interpolator.interpolate("x=${x} y=${y} z=${Z}"), "x=X y=Y z=${Z}");

      model.set("z", "Z");

      assertEquals(interpolator.interpolate("x=${x} y=${y} z=${z}"), "x=X y=Y z=Z");

      model.set("x", "X overridden by model");

      assertEquals(interpolator.interpolate("x=${x} y=${y} z=${z}"), "x=X overridden by model y=Y z=Z");
   }

   public void testPartialToken() throws Exception {
      MockRequest request = new MockRequest("GET", "/?x=X&y=Y", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new RequestContextBuilder().build(request, response);
      TokenFilter filter = new ContextFilter(context);
      Interpolator interpolator = new Interpolator(filter);
      
      Model model = context.getModel();
      model.set("x", "X");
      model.set("y", "Y");
      model.set("z", "Z");

      assertEquals(interpolator.interpolate("$"), "$");
      assertEquals(interpolator.interpolate("$x"), "$x");
      assertEquals(interpolator.interpolate("${y"), "${y");
      assertEquals(interpolator.interpolate("${y}"), "Y");

   }

}
