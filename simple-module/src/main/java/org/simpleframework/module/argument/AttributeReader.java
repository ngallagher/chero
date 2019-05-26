package org.simpleframework.module.argument;

import java.util.Map;
import java.util.Set;

public interface AttributeReader {
   Map<String, String> read(Set<String> files);
   boolean exists(Set<String> files);
}
