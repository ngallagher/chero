package org.simpleframework.module.graph;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

   private final Map<String, Boolean> convertable;
   private final Map<String, Boolean> component;
   private final Map<String, Boolean> internal;
   private final Map<String, Boolean> visible;
   private final Map<String, Boolean> module;
   private final ComponentMapper mapper;
   private final Set<Class> types;
   private final ClassPath path;
   
   public ModuleFilter(ClassPath path, Set<Class> types) {
      this.mapper = new ComponentMapper();
      this.convertable = new HashMap<>();
      this.component = new HashMap<>();
      this.internal = new HashMap<>();
      this.visible = new HashMap<>();
      this.module = new HashMap<>();
      this.types = types;
      this.path = path;      
   }

   public boolean isMissing(ClassNode node) {
      if(convertable.isEmpty()) {
         for(Class type : PRIMITIVES) {
            String alias = type.getName();
            convertable.put(alias, true);               
         }
      }
      return !isConvertable(node) && !isInternal(node);
   }   
   
   public boolean isComponent(ClassNode node) {
      String name = node.getName();
      Boolean match = component.get(name);
      
      if(match == null) {
         Set<ClassNode> nodes = path.getTypes(Component.class);
         
         if(nodes.contains(node)) {
            component.put(name, true);
            return true;
         }
         component.put(name, false);
         return false;
      }
      return match.booleanValue();
   }
   
   public boolean isModule(ClassNode node) {
      String name = node.getName();
      Boolean match = module.get(name);
      
      if(match == null) {
         Set<ClassNode> nodes = path.getTypes(Module.class);
         
         if(nodes.contains(node)) {
            module.put(name, true);
            return true;
         }
         module.put(name, false);
         return false;
      }
      return match.booleanValue();
   }
   
   public boolean isVisible(ClassNode node) {
      String name = node.getName();
      Boolean match = visible.get(name);
      
      if(match == null) {
         Set<String> packages = path.getPackages();
         
         for(String prefix : packages) {
            if(name.startsWith(prefix)) {
               visible.put(name, true);
               return true;
            }
         }
         visible.put(name, false);
         return false;
      }
      return match.booleanValue();  
   }
   
   public boolean isInternal(ClassNode node) {
      String name = node.getName();
      Boolean match = internal.get(name);
      
      if(match == null) {
         if(isAvailable(node)) {
            internal.put(name, true);
            return true;
         }
         internal.put(name, false);
         return false;
      }
      return match.booleanValue();
   }

   private boolean isAvailable(ClassNode node) {
      String name = node.getName();

      for(Class type : types) {
         Set<String> names = mapper.expand(type)
            .stream()
            .map(Class::getName)               
            .collect(Collectors.toSet());
         
         if(names.contains(name)) {
            return true;
         }
      }         
      return true;
   }
   
   private boolean isConvertable(ClassNode node) {  
      String name = node.getName();
      Boolean match = convertable.get(name);
      
      if(match == null) {
         if(node.isEnum()) {
            convertable.put(name, true);
            return true;
         }
         convertable.put(name, false);
         return false;
      }
      return match.booleanValue();
   }
}
