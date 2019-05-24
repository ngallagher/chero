package org.simpleframework.module.argument;

import java.util.Map;

public interface AttributeReader {
   Map<String, String> read(String... path);
   boolean exists(String... path);
}
