package org.simpleframework.module.resource.action.context;

import org.simpleframework.module.resource.action.build.MockRequest;
import org.simpleframework.module.resource.action.build.MockResponse;
import org.simpleframework.service.context.Context;
import org.simpleframework.service.context.Interpolator;
import org.simpleframework.service.context.Model;
import org.simpleframework.service.resource.action.ActionContextBuilder;

import junit.framework.TestCase;

public class InterpolatorTest extends TestCase {

   public void testInterpolation() throws Exception {
      MockRequest request = new MockRequest("GET", "/a/b/c/showA?x=X&y=Y", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new ActionContextBuilder().build(request, response);
      Interpolator interpolator = new Interpolator(context);

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
      Context context = new ActionContextBuilder().build(request, response);
      Interpolator interpolator = new Interpolator(context);
      
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
