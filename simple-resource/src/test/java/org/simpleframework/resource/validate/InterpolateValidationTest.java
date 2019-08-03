package org.simpleframework.resource.validate;

import java.util.Iterator;

import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.ContextValidation;
import org.simpleframework.module.core.Validation;
import org.simpleframework.resource.action.RequestContextBuilder;
import org.simpleframework.resource.build.MockRequest;
import org.simpleframework.resource.build.MockResponse;

import junit.framework.TestCase;

public class InterpolateValidationTest extends TestCase {

   public void testValidation() throws Exception {
      MockRequest request = new MockRequest("GET", "/a/b/c/showA?x=X&y=Y", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new RequestContextBuilder().build(request, response);
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
