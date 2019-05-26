package org.simpleframework.module.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

public class DependencyTreeScanner {
   
   public DependencyTree scan(ClassPath path, Set<Class> internal) {
      Set<String> names = internal.stream()
            .map(Class::getName)
            .collect(Collectors.toSet());
      
      LinkedList<Class> ready = new LinkedList<>();
      LinkedList<ClassNode> resolving = new LinkedList<>();
      Set<ClassNode> done = new HashSet<>();
      DependencyCollector collector = new DependencyCollector(path, names);
      resolving.addAll(path.getTypes(Component.class));
      
      while(!resolving.isEmpty()) {
         ClassNode next = resolving.poll();
         String name = next.getName();
         Set<ClassNode> children = collector.collect(name);
         
         children.removeAll(done);
         System.err.println(next);
         
         if(!children.isEmpty()) {
            for(ClassNode child : children) {
               resolving.offer(child);
            }
            resolving.offer(next);
         } else {
            if(done.add(next)) {
               Class type = next.getType();
               ready.offer(type);
            }
         }
      }
      return new DependencyTree(path, ready);
   }
   
//   private Set<ClassNode> getChildren(ClassNode info, ClassPath path, Predicate<String> filter, Set<String> names) {
//      Set<ClassNode> done = new HashSet<>();
//      List<ConstructorNode> constructors = info.getConstructors();
//      int size = constructors.size();
//      
//      for(int i = 0; i < size; i++) {
//         ConstructorNode constructor = constructors.get(i);
//         List<ClassNode> params = constructor.getParameterTypes();
//         
//         for(ClassNode param : params) {
//            String name = param.getName();
//            ClassNode paramInfo = path.getType(name);
//            
//            if(filter.test(name)) {
//               if(paramInfo != null && !paramInfo.isEnum() && !names.contains(name)) {
//                  paramInfo = componentType(paramInfo, path);
//                  
//                  if(paramInfo == null) {
//                     throw new RuntimeException("Could not resolve type for " + name);
//                  }
//                  done.add(paramInfo);
//               } 
//            } else {
//               if(!names.contains(name) && !name.startsWith("java.") && name.contains(".")) {
//                  ClassNode node = anyTypeImplementing(name, path, names);
//                  
//                  if(node == null) {
//                     throw new RuntimeException("Could not resolve type for " + name);
//                  }
//                  //done.add(node);
//               }
//            }
//         }
//      }
//      return done;
//   }
//   
//   private ClassNode anyTypeImplementing(String param, ClassPath path, Set<String> names) {
//      ClassNode node = path.getType(param);
//      if(node != null) {
//         return node.getImplementations()
//            .stream()
//            .filter(entry -> names.contains(entry.getName()))
//            .findFirst()
//            .orElse(null);
//      }
//      return null;
//   }
//   
//   private ClassNode componentType(ClassNode info, ClassPath path) {
//      String rootName = info.getName();
//
//      if(!path.getTypes(Component.class).contains(info)) {
//         if(info.isInterface()) {
//            return info.getImplementations()
//                  .stream()
//                  .findFirst()
//                  .orElse(null);
//         }
//         for(ClassNode next : path.getTypes(Component.class)) {
//            if(next.isSuper(rootName)) {
//               return next;
//            }
//         }
//         return null;
//      }
//      return info;
//   }
}
