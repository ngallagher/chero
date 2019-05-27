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
            String key = name + "." + entry.getKey();
            Object value = entry.getValue();

            collect(map, key, value);
         }
      } else if (Collection.class.isInstance(object)) {
         Collection values = Collection.class.cast(object);

         for (Object value : values) {
            collect(map, name, value);
         }
      } else {
         String value = String.valueOf(object);
         String token = value.trim();
         
         map.put(name, token);
      }
   }
}
