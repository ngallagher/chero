package org.simpleframework.module.index;

import java.util.Map;

import io.github.classgraph.ClassInfo;

interface ClassFinder {
   ClassInfo find(String name);
   Map<String, ClassInfo> findAll();
}
