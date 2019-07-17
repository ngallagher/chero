package org.simpleframework.module.resource.action.build;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleframework.module.resource.action.Schema;

public class MethodMatchIndexer {

   private final List<MethodIndex> indexes;
   private final ActionScanner scanner;
   private final ClassFinder finder;
   private final Schema schema;

   public MethodMatchIndexer(ActionScanner scanner, ClassFinder finder) {
      this(scanner, finder, null);
   }
   
   public MethodMatchIndexer(ActionScanner scanner, ClassFinder finder, Schema schema) {
      this.indexes = new LinkedList<MethodIndex>();
      this.scanner = scanner;
      this.finder = finder;
      this.schema = schema;
   }

   public synchronized List<MethodMatch> matches() throws Exception {
      if (indexes.isEmpty()) {
         Set<Class> components = finder.getComponents();

         for (Class component : components) {
            Map<String, List<MethodDispatcher>> extracted = scanner.createDispatchers(component);
            Set<String> patterns = extracted.keySet();

            for (String pattern : patterns) {
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
      Collections.sort(indexes);

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
      private final String expression;
      private final Pattern pattern;
      private final int length;

      public MethodIndex(Collection<MethodDispatcher> dispatchers, String pattern) {
         this.dispatchers = Collections.unmodifiableCollection(dispatchers);
         this.pattern = Pattern.compile(pattern);
         this.length = pattern.length();
         this.expression = pattern;
      }
      
      @Override
      public Iterable<MethodDispatcher> actions() {
         return dispatchers;
      }

      public boolean matches(String path) {
         Matcher matcher = pattern.matcher(path);

         if (matcher.matches()) {
            return true;
         }
         return false;
      }

      @Override
      public int compareTo(MethodMatch match) {
         int other = match.length();
         
         if (length < other) {
            return 1;
         }
         if (length == other) {
            return 0;
         }
         return -1;
      }
      
      @Override
      public String expression() {
         return pattern.pattern();
      }
      
      @Override
      public int length() {
         return length;
      }

      @Override
      public String toString() {
         return String.format("'%s' -> %s", expression, dispatchers);
      }
   }
}
