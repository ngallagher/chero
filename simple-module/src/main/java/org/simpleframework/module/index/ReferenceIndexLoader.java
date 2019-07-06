package org.simpleframework.module.index;

import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.simpleframework.module.common.Cache;
import org.simpleframework.module.common.CopyOnWriteCache;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;

class ReferenceIndexLoader {
   
   private final Function<String, Reference> builder;
   private final Cache<String, Reference> index;
   
   public ReferenceIndexLoader(ClassPath path) {
      this.builder = name -> new Reference(path, name);
      this.index = new CopyOnWriteCache<>();
   }

   public ClassNode loadNode(String name) {
      return index.fetch(name, builder).getNode();
   }
   
   private static class Reference {
      
      private final AtomicReference<Class> reference;
      private final ClassNode node;
      private final String name;
      
      public Reference(ClassPath path, String name) {
         this.reference = new AtomicReference<>();
         this.node = new ReferenceNode(this, name);
         this.name = name;
      }       
      
      public Class<?> getType() {
         try {
            Class type = reference.get();
            
            if(type == null) {
               type = Class.forName(name);
               reference.set(type);
            }
            return type;
         } catch(Exception e) {
            return null;
         }
      }
      
      public ClassNode getNode() {
         return node;
      }    
   }
   
   private static class ReferenceNode implements ClassNode {
      
      private final Reference reference;
      private final String name;
      
      public ReferenceNode(Reference reference, String name) {
         this.reference = reference;
         this.name = name;
      }

      @Override
      public URL getResource() {
         return null;
      }
      
      @Override
      public String getName() {
         return name;
      }
      
      @Override
      public Class<?> getType() {
         return reference.getType();
      }
      
      @Override
      public String toString() {
         return name;
      }
   }
}
