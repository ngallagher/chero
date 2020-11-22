package org.simpleframework.module.index;

import org.simpleframework.module.common.CacheValue;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.path.FieldNode;

import io.github.classgraph.FieldInfo;

class FieldIndex implements FieldNode {

   private final CacheValue<ClassNode> type;
   private final ClassNode parent;
   private final ClassPath path;
   private final FieldInfo info;
   
   public FieldIndex(ClassPath path, ClassNode parent, FieldInfo info) {
      this.type = new CacheValue<>();
      this.parent = parent;
      this.path = path;
      this.info = info;
   }
   
   @Override
   public boolean isAnnotationPresent(String name) {
      return info.hasAnnotation(name);
   }
   
   @Override
   public ClassNode getType() {
      return type.get(() -> {
         String name = info.getClassInfo().getName();
         int index = name.indexOf("<");
   
         if(index != -1) {
            String type = name.substring(0, index);
            return path.findType(type);
         }
         return path.findType(name);
      });
   }

   @Override
   public ClassNode getDeclaringClass() {
      return parent;
   }
   
   @Override
   public String getName() {
      return info.getName();
   }
   
   @Override
   public String toString() {
      return info.toString();
   }
}
