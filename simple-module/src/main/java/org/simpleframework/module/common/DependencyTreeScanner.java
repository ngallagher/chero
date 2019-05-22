package org.simpleframework.module.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.MethodParameterInfo;

public class DependencyTreeScanner {
   
   private final DependencyPathBuilder builder;

   public DependencyTreeScanner(Class<?>... modules) {
      this.builder = new DependencyPathBuilder(modules);
   }
   
   public DependencyTree scan(Class<?>... internal) {
      DependencyPath path = builder.create();
      Set<String> names = Arrays.asList(internal)
            .stream()
            .map(Class::getName)
            .collect(Collectors.toSet());
      
      LinkedList<Class> ready = new LinkedList<>();
      LinkedList<ClassInfo> resolving = new LinkedList<>();
      Set<ClassInfo> done = new HashSet<>();
      
      resolving.addAll(path.getComponents().values());
      
      while(!resolving.isEmpty()) {
         ClassInfo next = resolving.poll();
         Set<ClassInfo> children = getChildren(next, path, names);
         
         children.removeAll(done);
         
         if(!children.isEmpty()) {
            for(ClassInfo child : children) {
               resolving.offer(child);
            }
            resolving.offer(next);
         } else {
            if(done.add(next)) {
               Class type = next.loadClass();
               ready.offer(type);
            }
         }
      }
      return new DependencyTree(path, ready);
   }
   
   private Set<ClassInfo> getChildren(ClassInfo info, DependencyPath path, Set<String> names) {
      Set<ClassInfo> done = new HashSet<>();
      MethodInfoList constructors = info.getConstructorInfo();
      Predicate<String> predicate = path.getPredicate();
      int size = constructors.size();
      
      for(int i = 0; i < size; i++) {
         MethodInfo constructor = constructors.get(i);
         MethodParameterInfo[] params = constructor.getParameterInfo();
         
         for(MethodParameterInfo param : params) {
            String name = param.getTypeDescriptor().toString();
            
            if(predicate.test(name)) {
               ClassInfo paramInfo = path.getObjects().get(name);
               
               if(paramInfo != null && !paramInfo.isEnum() && !names.contains(name)) {
                  paramInfo = componentType(paramInfo, path);
                  
                  if(paramInfo == null) {
                     throw new RuntimeException("Could not resolve type for " + name);
                  }
                  done.add(paramInfo);
               } 
            } else {
               // XXX here we need to throw an exception if there is no way to resolve the component.......
            }
         }
      }
      return done;
   }
   
   private ClassInfo componentType(ClassInfo info, DependencyPath path) {
      String rootName = info.getName();

      if(!path.getComponents().containsKey(rootName)) {
         if(info.isInterface()) {
            ClassInfoList list = info.getClassesImplementing();
            for(int i = 0; i < list.size(); i++) {
               ClassInfo next = list.get(i);
               String name = next.getName();
               
               if(path.getComponents().containsKey(name)) {
                  return path.getComponents().get(name);
               }
            }
            return null;
         }
         for(ClassInfo next : path.getComponents().values()) {
            if(next.extendsSuperclass(rootName)) {
               return next;
            }
         }
         return null;
      }
      return path.getComponents().get(rootName);
   }
}
