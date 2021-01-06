package org.simpleframework.module.argument;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum Environment {
   LOCAL("local"),
   DEV("dev"),
   DEMO("demo"),
   TEST("test"),
   SIM("sim"),
   PROD("prod");

   public final List<String> codes;
   public final String code;

   private Environment(String code) {
      this.codes = unmodifiableList(asList("common", code));
      this.code = code;
   }

   public String code() {
      return code;
   }

   public Set<String> codes() {
      return new HashSet<>(codes);
   }

   public static Environment resolve(String token) {
      Environment[] environments = Environment.values();

      for(int i = 0; i < environments.length; i++) {
         if(environments[i].code.equalsIgnoreCase(token)) {
            return environments[i];
         }
      }
      throw new IllegalStateException("Environment '" + token + "' not supported");
   }
}
