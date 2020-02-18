package org.simpleframework.resource.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

public class MethodMatchComparatorTest extends TestCase {
   
   public void testComparator() throws Exception {
      MethodMatchComparator comparator = new MethodMatchComparator();
      List<MockMethodMatch> matches = new ArrayList<>();
      
      matches.add(new MockMethodMatch("/[0-9]+", true));
      matches.add(new MockMethodMatch("/a", false));
      matches.add(new MockMethodMatch("/wallet/all", false));
      matches.add(new MockMethodMatch("/wallet/(.*)", true));

      
      Collections.sort(matches, comparator);
      
      assertEquals(matches.get(0).pattern.path(), "/wallet/all");
      assertEquals(matches.get(1).pattern.path(), "/a");
      assertEquals(matches.get(2).pattern.path(), "/wallet/(.*)");         
      assertEquals(matches.get(3).pattern.path(), "/[0-9]+");   
      
      
   }
   
   private static class MockMethodMatch implements MethodMatch {

      private final MethodPattern pattern;
      
      public MockMethodMatch(String pattern, boolean expression) {
         this.pattern = new MethodPattern(pattern, expression);         
      }
      
      @Override
      public Iterable<MethodDispatcher> actions() {
         return null;
      }

      @Override
      public boolean matches(String path) {
         return false;
      }

      @Override
      public MethodPattern pattern() {
         return pattern;
      }
      
   }

}
