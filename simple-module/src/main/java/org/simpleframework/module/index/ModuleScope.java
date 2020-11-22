package org.simpleframework.module.index;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.simpleframework.module.common.CacheValue;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

class ModuleScope {

   private final CacheValue<Set<String>> prefixes;
   private final PackageList packages;
   private final ClassFinder finder;

   public ModuleScope(Set<Class> modules) {
      this.finder = new ClassGraphFinder(3);
      this.packages = new PackageList(finder, modules);
      this.prefixes = new CacheValue<>();
   }

   public Set<String> getPackages() {
      return prefixes.get(() -> packages.list());
   }

   public Collection<ClassInfo> getClasses() {
      return finder.findAll().values();
   }

   private static class ClassGraphFinder implements ClassFinder {

      private final Map<String, ClassInfo> index;
      private final AtomicInteger retries;
      private final ReentrantLock lock;

      public ClassGraphFinder(int retries) {
         this.retries = new AtomicInteger(retries);
         this.index = new ConcurrentHashMap<>();
         this.lock = new ReentrantLock();
      }

      @Override
      public ClassInfo find(String name) {
         return findAll().get(name);
      }

      @Override
      public Map<String, ClassInfo> findAll() {
         while (index.isEmpty()) {
            int remaining = retries.decrementAndGet();

            if (remaining <= 0) {
               throw new IllegalArgumentException("Could not find any classes");
            }
            lock.lock();

            try {
               ScanResult result = new ClassGraph()
                    .removeTemporaryFilesAfterScan()
                    .enableAllInfo()
                    .acceptPaths("..")
                    .scan();

               index.putAll(result.getAllClassesAsMap());
            } finally {
               lock.unlock();
               Thread.yield();
            }
         }
         return Collections.unmodifiableMap(index);
      }
   }
}
