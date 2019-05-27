package org.simpleframework.module.index;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.core.ComponentMapper;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

public class ModuleFilter {
   
   private static final Class[] PRIMITIVES = {
      String.class,
      Integer.class,
      Long.class,
      Float.class,
      Double.class,
      Short.class,
      Byte.class,
      Character.class,
      Boolean.class,
      List.class,
      File.class,
      URI.class,
      Class.class,
      int.class,
      long.class,
      float.class,
      double.class,
      short.class,
      byte.class,
      char.class,
      boolean.class         
   };

   private final Map<ClassNode, Boolean> component;
   private final Map<ClassNode, Boolean> internal;
   private final Map<ClassNode, Boolean> visible;
   private final Map<ClassNode, Boolean> missing;
   private final Map<ClassNode, Boolean> module;
   private final ComponentMapper mapper;
   private final Set<String> convertable;
   private final Set<Class> types;
   private final ClassPath path;
   
   public ModuleFilter(ClassPath path, Set<Class> types) {
      this.mapper = new ComponentMapper();
      this.convertable = new HashSet<>();
      this.component = new HashMap<>();
      this.internal = new HashMap<>();
      this.missing = new HashMap<>();
      this.visible = new HashMap<>();
      this.module = new HashMap<>();
      this.types = types;
      this.path = path;      
   }  
   
   public boolean isComponent(ClassNode node) {
      return component.computeIfAbsent(node, key -> 
             path.getTypes(Component.class).contains(node));
      
   }
   
   public boolean isModule(ClassNode node) {
      return module.computeIfAbsent(node, key -> 
         path.getTypes(Module.class).contains(node));
   }
   
   public boolean isVisible(ClassNode node) {
      return visible.computeIfAbsent(node, key -> {
         String name = node.getName();
         return path.getPackages()
               .stream()
               .anyMatch(prefix -> name.startsWith(prefix)); 
      });
   }
   
   public boolean isInternal(ClassNode node) {
      return internal.computeIfAbsent(node, key -> {
         String name = node.getName();
         return types.stream()
               .map(mapper::expand)
               .flatMap(Set<Class>::stream)
               .map(Class::getName)               
               .anyMatch(type -> type.equals(name)); 
      });
   }
   
   public boolean isMissing(ClassNode node) {     
      if(convertable.isEmpty()) {
         Arrays.asList(PRIMITIVES)
            .stream()
            .map(Class::getName)
            .forEach(convertable::add);
      }
      String name = node.getName();
      return missing.computeIfAbsent(node, key -> 
            !convertable.contains(name) && 
            !node.isEnum() && 
            !isInternal(node)); 
   } 
}
