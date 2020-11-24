package org.simpleframework.module.core;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EscapeType {
   NONE(".*") {
      @Override
      public String escape(String value) {
         return value;
      }
   },
   JSON(".json") {
      @Override
      public String escape(String value) {
         return value.replace("\\", "\\\\").replace("\"", "\\\"");
      }
   };

   public final String extension;

   private EscapeType(String extension) {
      this.extension = extension;
   }

   public abstract String escape(String value);

   public static EscapeType resolve(String path) {
      EscapeType[] types = EscapeType.values();

      for(EscapeType type : types) {
         if(path.endsWith(type.extension)) {
            return type;
         }
      }
      return EscapeType.NONE;
   }
}

