package org.simpleframework.module.resource.action.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.LeastRecentlyUsedCache;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.resource.action.Schema;

public class MethodDispatcherResolver implements MethodResolver {

   private final Cache<String, MatchGroup> cache;
   private final MethodMatchIndexer matcher;
   private final PathResolver resolver;

   public MethodDispatcherResolver(ActionScanner scanner, ClassFinder finder) {
      this(scanner, finder, null);
   }
   
   public MethodDispatcherResolver(ActionScanner scanner, ClassFinder finder, Schema schema) {
      this.cache = new LeastRecentlyUsedCache<String, MatchGroup>(5000);
      this.matcher = new MethodMatchIndexer(scanner, finder, schema);
      this.resolver = new PathResolver();
   }

   @Override
   public MethodDispatcher resolveBest(Context context) throws Exception {
      MatchGroup group = match(context);

      if (group != null) {
         MethodDispatcher result = null;
         float best = 0f;

         for (MethodMatch match : group.matches) {
            Iterable<MethodDispatcher> dispatchers = match.actions();
            String pattern = match.expression();
            int length = pattern.length();
            
            for (MethodDispatcher dispatcher : dispatchers) {
               float score = dispatcher.score(context);
               float value = score + length;
               
               if (value > best) {
                  result = dispatcher;
                  best = value;
               }
            }
         }
         if (result == null) {
            for (MethodMatch match : group.matches) {
               Iterable<MethodDispatcher> dispatchers = match.actions();
               
               for (MethodDispatcher dispatcher : dispatchers) {
                  float score = dispatcher.score(context);
                  
                  if(score >= 0) {
                     return dispatcher;
                  }
               }
            }
         }
         return result;
      }
      return null;
   }

   @Override
   public List<MethodDispatcher> resolveBestFirst(Context context) throws Exception {
      MatchGroup group = match(context);

      if (group != null) {
         List<MethodDispatcher> list = new ArrayList<MethodDispatcher>();

         for (MethodMatch match : group.matches) {
            Iterable<MethodDispatcher> dispatchers = match.actions();
            
            for (MethodDispatcher dispatcher : dispatchers) {
               float score = dispatcher.score(context);

               if (score != -1) {
                  list.add(dispatcher);
               }
            }
         }
         return list;
      }
      return Collections.emptyList();
   }

   @Override
   public List<MethodDispatcher> resolveBestLast(Context context) throws Exception {
      MatchGroup group = match(context);

      if (group != null) {
         LinkedList<MethodDispatcher> list = new LinkedList<MethodDispatcher>();

         for (MethodMatch match : group.matches) {
            Iterable<MethodDispatcher> dispatchers = match.actions();
            
            for (MethodDispatcher dispatcher : dispatchers) {
               float score = dispatcher.score(context);

               if (score != -1) {
                  list.addFirst(dispatcher);
               }
            }
         }
         return list;
      }
      return Collections.emptyList();
   }
   
   private MatchGroup match(Context context) throws Exception {
      String normalized = resolver.resolve(context);

      if (!cache.contains(normalized)) {
         List<MethodMatch> matches = matcher.matches();

         if (!matches.isEmpty()) {
            MatchGroup group = new MatchGroup(normalized);

            for (MethodMatch match : matches) {
               if (match.matches(normalized)) {
                  group.add(match);
               }
            }
            cache.cache(normalized, group);
         }
      }
      return cache.fetch(normalized);
   }

   public static class MatchGroup implements Iterable<MethodMatch> {

      private final List<MethodMatch> matches;
      private final String path;

      public MatchGroup(String path) {
         this.matches = new ArrayList<MethodMatch>();
         this.path = path;
      }

      public Iterator<MethodMatch> iterator() {
         return matches.iterator();
      }

      public void add(MethodMatch match) {
         matches.add(match);
         Collections.sort(matches); // slow ?
      }
   }
}
