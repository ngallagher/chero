package org.simpleframework.resource.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.LeastRecentlyUsedCache;
import org.simpleframework.module.core.Context;

public class MethodDispatcherResolver implements MethodResolver {

   private final Cache<String, List<MethodMatch>> cache;
   private final Comparator<MethodMatch> comparator;
   private final MethodMatchIndexer indexer;
   private final PathResolver resolver;

   public MethodDispatcherResolver(MethodMatchIndexer indexer) {
      this.cache = new LeastRecentlyUsedCache<String, List<MethodMatch>>(5000);
      this.comparator = new MethodMatchComparator();
      this.resolver = new PathResolver();
      this.indexer = indexer;
   }

   @Override
   public MethodDispatcher resolveBest(Context context) throws Exception {
      List<MethodMatch> matches = matchBestFirst(context);

      if (matches != null) {
         MethodDispatcher result = null;
         float best = 0f;

         for (MethodMatch match : matches) {
            Iterable<MethodDispatcher> dispatchers = match.actions();
            MethodPattern pattern = match.pattern();
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
            for (MethodMatch match : matches) {
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
      List<MethodMatch> matches = matchBestFirst(context);

      if (matches != null) {
         List<MethodDispatcher> list = new ArrayList<MethodDispatcher>();

         for (MethodMatch match : matches) {
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
      List<MethodMatch> matches = matchBestFirst(context);

      if (matches != null) {
         LinkedList<MethodDispatcher> list = new LinkedList<MethodDispatcher>();

         for (MethodMatch match : matches) {
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
   
   private List<MethodMatch> matchBestFirst(Context context) throws Exception {
      String normalized = resolver.resolve(context);

      if (!cache.contains(normalized)) {
         List<MethodMatch> matches = indexer.matches();

         if (!matches.isEmpty()) {
            List<MethodMatch> group = new ArrayList<MethodMatch>();

            for (MethodMatch match : matches) {
               if (match.matches(normalized)) {
                  group.add(match);
               }
            }
            List<MethodMatch> value = Collections.unmodifiableList(group);
            
            if(!group.isEmpty()) {
               Collections.sort(group, comparator);
            }
            cache.cache(normalized, value); // cache immutable
         }
      }
      return cache.fetch(normalized);
   }
}
