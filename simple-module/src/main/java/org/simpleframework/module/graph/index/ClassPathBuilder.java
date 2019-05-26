package org.simpleframework.module.graph.index;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.simpleframework.module.graph.DependencyGraph;
import org.simpleframework.module.graph.DependencyGraphBuilder;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodParameterInfo;
import io.github.classgraph.TypeSignature;

public class ClassPathBuilder {

   private final DependencyGraphBuilder builder;

   public ClassPathBuilder(Set<Class> modules) {
      this(modules, Collections.EMPTY_SET);
   }
   
   public ClassPathBuilder(Set<Class> modules, Set<String> paths) {
      this.builder = new DependencyGraphBuilder(modules, paths);
   }

   public ClassPath create() {
      DependencyGraph graph = builder.create();
      Predicate<String> predicate = graph.getPredicate();
      ImportPath path = new ImportPath(predicate);
      Iterator<ImportClassNode> iterator = graph.getGraph()
            .scan()
            .getAllClasses()
            .stream()
            .map(info -> new ImportClassNode(path, info))            
            .iterator();

      if (iterator.hasNext()) {         
         while (iterator.hasNext()) {
            ImportClassNode next = iterator.next();
            String name = next.getName();
            
            path.objects.put(name, next);
         }
         return path;
      }
      return new ImportPath(name -> false);
   }
   
   private static class ImportConstructorNode extends ImportMethodNode implements ConstructorNode {

      public ImportConstructorNode(ClassPath path, ClassNode parent, MethodInfo info) {
         super(path, parent, info);
      }
      
      @Override
      public ClassNode getReturnType() {
         return getDeclaringClass(); 
      }
   }
   
   private static class ImportMethodNode implements MethodNode {
      
      private final ClassNode parent;
      private final ClassPath path;
      private final MethodInfo info;
      
      public ImportMethodNode(ClassPath path, ClassNode parent, MethodInfo info) {
         this.parent = parent;
         this.path = path;
         this.info = info;
      }
      
      @Override
      public ClassNode getReturnType() {
         String name = info.getTypeSignatureOrTypeDescriptor().getResultType().toString();
         int index = name.indexOf("<");

         if(index != -1) {
            String type = name.substring(0, index);
            return path.getType(type);
         }
         return path.getType(name);
      }

      @Override
      public List<ClassNode> getParameterTypes() {
         MethodParameterInfo[] params = info.getParameterInfo();         
         return Arrays.asList(params)
               .stream() 
               .filter(Objects::nonNull)
               .map(MethodParameterInfo::getTypeDescriptor)
               .map(TypeSignature::toString)
               .map(path::getType)
               .filter(Objects::nonNull)
               .collect(Collectors.toList());
      }

      @Override
      public ClassNode getDeclaringClass() {
         return parent;
      }
      
      @Override
      public String toString() {
         return info.toString();
      }
   }
   
   private static class ImportArrayNode implements ArrayNode {
      
      private final ClassNode element;
      private final int[] dimensions;

      public ImportArrayNode(ClassNode element, int width) {
         this.dimensions = new int[width];
         this.element = element;
      }
      
      @Override
      public Class getType() {
         Class type = element.getType();         
         Object empty = Array.newInstance(type, dimensions);
         
         return empty.getClass();
      }
      
      @Override
      public String getName() {
         return element + Arrays.asList(dimensions)
            .stream()
            .map(value -> "[]")
            .collect(Collectors.joining(""));
      }
      
      @Override
      public URL getResource() {
         return element.getResource();
      }      
      
      @Override
      public boolean isArray() {
         return true;
      }

      @Override
      public int getDimensions() {         
         return dimensions.length;
      }
   }
   
   private static class ImportClassNode implements ClassNode {
      
      private final ClassPath path;
      private final ClassInfo info;
      
      public ImportClassNode(ClassPath path, ClassInfo info) {
         this.path = path;
         this.info = info;
      }
      
      @Override
      public boolean isEnum() {
         return info.isEnum();
      }
      
      @Override
      public boolean isInterface() {
         return info.isInterface();
      }
      
      @Override
      public boolean isAnnotationPresent(String name) {
         return info.hasAnnotation(name);
      }
      
      @Override
      public boolean isSuper(String name) {
         return info.extendsSuperclass(name);
      }     

      @Override
      public List<MethodNode> getMethods() {
         return info.getMethodInfo()
               .stream()
               .map(method -> new ImportMethodNode(path, this, method))
               .collect(Collectors.toList());
      }

      @Override
      public List<ConstructorNode> getConstructors() {
         return info.getConstructorInfo()
               .stream()
               .map(constructor -> new ImportConstructorNode(path, this, constructor))
               .collect(Collectors.toList());
      }
      
      @Override
      public List<ClassNode> getImplementations() {
         return info.getClassesImplementing()
               .stream()
               .map(ClassInfo::getName)
               .map(path::getType)
               .collect(Collectors.toList());
      }

      @Override
      public URL getResource() {
         return info.getClasspathElementURL();
      }
      
      @Override
      public String getName() {
         return info.getName();
      }

      @Override
      public Class getType() {
         return info.loadClass();
      }
      
      @Override
      public String toString() {
         return getName();
      }
   }

   private static class ImportPath implements ClassPath {
      
      private final Map<Class, Set<ClassNode>> indexes;
      private final Map<String, ClassNode> objects;
      private final Predicate<String> predicate;
      private final SystemClassLoader loader;
      
      public ImportPath(Predicate<String> predicate) {
         this.loader = new SystemClassLoader(this);
         this.indexes = new ConcurrentHashMap<>();
         this.objects = new HashMap<>();
         this.predicate = predicate;
      }
      
      @Override
      public ClassNode getType(String name, int dimensions) {
         ClassNode node = objects.get(name);
         
         if(node != null) {
            return new ImportArrayNode(node, dimensions);
         }
         return null;
      }
      
      @Override
      public ClassNode getType(String name) {
         ClassNode node = objects.get(name);
         
         if(node == null) {
            return loader.loadClass(name);
         }
         return node;
      }
      
      @Override
      public Set<ClassNode> getTypes(Class<? extends Annotation> type) {
         Set<ClassNode> matches = indexes.get(type);
         
         if(matches == null) {
            String name = type.getName();
            Predicate<ClassNode> filter = node -> {
               String qualifier = node.getName();         
            
               if(predicate.test(qualifier)) {
                  return node.isAnnotationPresent(name);
               }
               return false;
            };
            matches = objects.values()
                  .stream()
                  .filter(filter)
                  .collect(Collectors.toSet());
            indexes.put(type, Collections.unmodifiableSet(matches));
         }
         return matches;
      }      
      
      @Override
      public Set<ClassNode> getTypes(Predicate<ClassNode> filter) {
         return objects.values()
               .stream()
               .filter(filter)
               .collect(Collectors.toSet());
      }      
      
      @Override
      public Set<ClassNode> getTypes() {
         return objects.values()
               .stream()
               .collect(Collectors.toSet());
      }
      
      @Override
      public Predicate<String> getPredicate() {
         return predicate;
      }
   }
}
