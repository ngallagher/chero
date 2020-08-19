package org.simpleframework.module.argument;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public enum Environment implements Iterable<String> {
   LOCAL("local"),
   DEVELOPMENT("dev"),
   DEMO("demo"),
   TEST("test"),
   SIMULATION("sim"),
   PRODUCTION("prod");

   private final List<String> names;

   private Environment(String name) {
      this.names = Arrays.asList("common", name);
   }

   @Override
   public Iterator<String> iterator() {
      return Collections.unmodifiableList(names).iterator();
   }
}
