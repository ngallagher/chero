package org.simpleframework.module.core;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;

public class ComponentStore implements ComponentManager {
   
   private final Function<Object, List<Object>> builder;
   private final Map<Class, List<Object>> groups;
   private final Map<Object, String> names;
   private final Set<Object> instances;
   private final ComponentMapper mapper;
   
   public ComponentStore() {
      this.groups = new ConcurrentHashMap<Class, List<Object>>();
      this.builder = (key) -> new CopyOnWriteArrayList<Object>();
      this.names = new ConcurrentHashMap<Object, String>();
      this.instances = new CopyOnWriteArraySet<Object>();
      this.mapper = new ComponentMapper();
   }
   
   @Override
   public <T> List<T> resolveAll(Class<T> type) {
      return (List)groups.computeIfAbsent(type, builder);
   }
   
   @Override
   public <T> T resolve(Class<T> type) {
      return resolve(type, null);
   }

   @Override
   public <T> T resolve(Class<T> type, String name) {
      return (T)instances.stream()
            .filter(Objects::nonNull)
            .filter(type::isInstance)
            .filter(object -> {
               String mapping = names.get(object);
               return Objects.equals(mapping, name);
            })
            .findFirst()
            .orElse(null);
   }

   @Override
   public void register(Object value) {
      register(value, null);
   }
   
   @Override
   public void register(Object value, String name) {
      if(instances.add(value)) { 
         if(name != null) {
            names.put(value, name);
         }
         mapper.expand(value)
            .stream()
            .map(this::resolveAll)
            .forEach(object -> List.class.cast(object).add(value));
      }
   }
}
