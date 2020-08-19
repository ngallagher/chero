package org.simpleframework.module.argument;

import java.util.Collections;

import org.simpleframework.module.core.Context;

import junit.framework.TestCase;

public class ContextBuilderTest extends TestCase {

   public void testConfig() {
      try {
         ContextBuilder builder = new ContextBuilder(Collections.singleton("C:\\Work\\temp"));
         Context context = builder.read(Collections.singleton("config"));
         
         assertEquals(context.getModel().get("job"), "Developer");
         assertEquals(context.getModel().get("languages.perl"), "Elite");
         assertEquals(context.getModel().get("languages.python"), "Elite");
         assertEquals(context.getModel().get("languages.pascal"), "Elite");
      }catch(Throwable e) {
         e.printStackTrace();
      }
   }
}
