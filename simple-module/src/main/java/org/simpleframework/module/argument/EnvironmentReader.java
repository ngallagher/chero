package org.simpleframework.module.argument;

import java.util.Collections;
import java.util.Set;

import org.simpleframework.module.core.Context;

public class EnvironmentReader {

   private final ContextBuilder reader;

   public EnvironmentReader() {
      this(Collections.EMPTY_SET);
   }

   public EnvironmentReader(Set<String> paths) {
      this.reader = new ContextBuilder(paths);
   }

   public Context read(Environment environment, String... arguments) {
      return reader.read(environment, arguments);
   }
}
