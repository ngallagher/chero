package org.simpleframework.resource.build;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleframework.resource.action.Schema;

public class MethodMatchIndexer {

   private final Comparator<MethodMatch> comparator;
   private final List<MethodIndex> indexes;
   private final ActionScanner scanner;
   private final ClassFinder finder;
   private final Schema schema;

   public MethodMatchIndexer(ActionScanner scanner, ClassFinder finder) {
      this(scanner, finder, null);
   }
   
   public MethodMatchIndexer(ActionScanner scanner, ClassFinder finder, Schema schema) {
      this.comparator = new MethodMatchComparator();
      this.indexes = new LinkedList<MethodIndex>();
      this.scanner = scanner;
      this.finder = finder;
      this.schema = schema;
   }

   public synchronized List<MethodMatch> matches() throws Exception {
      if (indexes.isEmpty()) {
         Set<Class> components = finder.getComponents();

         for (Class component : components) {
            Map<MethodPattern, List<MethodDispatcher>> extracted = scanner.createDispatchers(component);
            Set<MethodPattern> patterns = extracted.keySet();

            for (MethodPattern pattern : patterns) {
               Collection<MethodDispatcher> dispatchers = extracted.get(pattern);

               if (!dispatchers.isEmpty()) {
                  MethodIndex index = new MethodIndex(dispatchers, pattern);
                  indexes.add(index);
               }
               if(schema != null) {
                  for(MethodDispatcher dispatcher : dispatchers) {
                     dispatcher.define(schema);
                  }
               }
            }
         }
         order(indexes);
      }
      return Collections.unmodifiableList(indexes);
   }

   private synchronized void order(List<MethodIndex> indexes) throws Exception {
      Collections.sort(indexes, comparator);

      for (MethodIndex index : indexes) {
         String text = index.toString();
         int length = text.length();
         
         if(length > 0) {
            System.out.println(text);
         }
      }
   }

   private static class MethodIndex implements MethodMatch {

      private final Collection<MethodDispatcher> dispatchers;
      private final MethodPattern pattern;
      
      public MethodIndex(Collection<MethodDispatcher> dispatchers, MethodPattern pattern) {
         this.dispatchers = Collections.unmodifiableCollection(dispatchers);
         this.pattern = pattern;
      }
      
      @Override
      public Iterable<MethodDispatcher> actions() {
         return dispatchers;
      }

      @Override
      public boolean matches(String path) {
         String declaration = pattern.path();
         
         if(!path.equals(declaration)) {
            Pattern expression = pattern.pattern();
            Matcher matcher = expression.matcher(path);
   
            if (matcher.matches()) {
               return true;
            }
            return false;
         }
         return true;
      }
      
      @Override
      public MethodPattern pattern() {
         return pattern;
      }

      @Override
      public String toString() {
         return String.format("'%s' -> %s", pattern, dispatchers);
      }
   }
}
