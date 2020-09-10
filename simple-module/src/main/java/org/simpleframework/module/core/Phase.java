package org.simpleframework.module.core;

public enum Phase {
   SCORE,
   EXECUTE;

   public boolean isScore() {
      return this == SCORE;
   }

   public boolean isExecute() {
      return this == EXECUTE;
   }
}
