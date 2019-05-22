package org.simpleframework.module.resource.action.validate;

import java.util.Iterator;

import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.ContextValidation;
import org.simpleframework.module.context.Validation;
import org.simpleframework.module.resource.action.ActionContextBuilder;
import org.simpleframework.module.resource.action.build.MockRequest;
import org.simpleframework.module.resource.action.build.MockResponse;

import junit.framework.TestCase;

public class InterpolateValidationTest extends TestCase {

   public void testValidation() throws Exception {
      MockRequest request = new MockRequest("GET", "/a/b/c/showA?x=X&y=Y", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new ActionContextBuilder().build(request, response);
      Validation validation = new ContextValidation(context);

      context.getModel().set("x", "X");
      context.getModel().set("y", "Y");
      
      validation.addError("The value of ${x} is wrong");
      validation.addError("x=${x} y=${y} z=${z}");

      Iterator<String> iterator = validation.iterator();

      assertEquals(iterator.next(), "The value of X is wrong");
      assertEquals(iterator.next(), "x=X y=Y z=${z}");
   }
}
