package org.simpleframework.module;

import java.util.List;

public interface DependencyManager {
   <T> T resolve(Class<T> type);
   <T> T resolve(Class<T> type, String name);
   <T> List<T> resolveAll(Class<T> type);
   void register(Object value);
}
