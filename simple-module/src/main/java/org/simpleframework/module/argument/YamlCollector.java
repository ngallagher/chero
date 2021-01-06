package org.simpleframework.module.argument;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class YamlCollector {

   public void collect(Map<String, String> map, String name, Object object) {
      if (Map.class.isInstance(object)) {
         Set<Entry> entries = Map.class.cast(object).entrySet();

         for (Entry entry : entries) {
            Object suffix = entry.getKey();
            String key = name + "." + suffix;
            Object value = entry.getValue();

            collect(map, key, value);
         }
      } else if (Collection.class.isInstance(object)) {
         Collection values = Collection.class.cast(object);
         int count = 0;

         for (Object value : values) {
            collect(map, name + "." + count++, value);
         }
      } else {
         String value = String.valueOf(object);
         String token = value.trim();
         
         map.put(name, token);
      }
   }
}
