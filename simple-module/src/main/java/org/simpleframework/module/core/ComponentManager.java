package org.simpleframework.module.core;

import java.util.List;

public interface ComponentManager {
   <T> T resolve(Class<T> type);
   <T> T resolve(Class<T> type, String name);
   <T> List<T> resolveAll(Class<T> type);
   void register(Object value);
}
