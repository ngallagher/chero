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

   private final List<String> codes;
   private final String code;

   private Environment(String code) {
      this.codes = Arrays.asList("common", code);
      this.code = code;
   }

   @Override
   public Iterator<String> iterator() {
      return Collections.unmodifiableList(codes).iterator();
   }

   public String code() {
      return code;
   }
}
