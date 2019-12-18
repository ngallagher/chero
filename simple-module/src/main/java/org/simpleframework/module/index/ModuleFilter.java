package org.simpleframework.module.index;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.DependsOn;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.HashCache;
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

   private final Cache<ClassNode, Boolean> component;
   private final Cache<ClassNode, Boolean> dependent;
   private final Cache<ClassNode, Boolean> provided;
   private final Cache<ClassNode, Boolean> internal;
   private final Cache<ClassNode, Boolean> visible;
   private final Cache<ClassNode, Boolean> missing;
   private final Cache<ClassNode, Boolean> module;
   private final ProviderCollector collector;
   private final ComponentMapper mapper;
   private final Set<String> convertable;
   private final Set<Class> types;
   private final ClassPath path;
   
   public ModuleFilter(ClassPath path, Set<Class> types) {
      this.mapper = new ComponentMapper();
      this.collector = new ProviderCollector();
      this.convertable = new HashSet<>();
      this.component = new HashCache<>();
      this.dependent = new HashCache<>();
      this.provided = new HashCache<>();
      this.internal = new HashCache<>();
      this.missing = new HashCache<>();
      this.visible = new HashCache<>();
      this.module = new HashCache<>();
      this.types = types;
      this.path = path;      
   }  
   
   public boolean isDependency(ClassNode node) {
      return isComponent(node) || isProvided(node);
   }
   
   public boolean isComponent(ClassNode node) {
      return component.fetch(node, key -> 
             path.getTypes(Component.class).contains(node));   
   }
   
   public boolean isProvided(ClassNode node) {
      return provided.fetch(node, key -> 
            !collector.collect(this, path, node).isEmpty());
   }
   
   public boolean isDependent(ClassNode node) {
      return dependent.fetch(node, key -> 
             path.getTypes(DependsOn.class).contains(node));
      
   }
   
   public boolean isModule(ClassNode node) {
      return module.fetch(node, key -> 
         path.getTypes(Module.class).contains(node));
   }
   
   public boolean isVisible(ClassNode node) {
      return visible.fetch(node, key -> {
         String name = node.getName();
         return path.getPackages()
            .stream()
            .anyMatch(prefix -> name.startsWith(prefix)); 
      });
   }
   
   public boolean isInternal(ClassNode node) {
      return internal.fetch(node, key -> {
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
      return missing.fetch(node, key -> 
            !convertable.contains(name) && 
            !node.isEnum() && 
            !node.isArray() &&
            !isInternal(node) &&
            !isProvided(node)); 
   } 
}
