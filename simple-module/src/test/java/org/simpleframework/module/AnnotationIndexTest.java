package org.simpleframework.module;

import java.util.HashSet;
import java.util.Set;

import org.simpleframework.module.annotation.Import;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.index.ClassPathBuilder;

import junit.framework.TestCase;

@Module("blah")
@Import(AnnotationIndexTest.class)
public class AnnotationIndexTest extends TestCase {
   
   public void testAnnotations() {
      Set<Class> modules = new HashSet<>();
      ClassPathBuilder builder = new ClassPathBuilder(modules);
      
      modules.add(AnnotationIndexTest.class);
      
      Class[] types = builder.create()
         .findType(AnnotationIndexTest.class.getName())
         .getAnnotation(Import.class)
         .value();
   
      assertEquals(types[0], AnnotationIndexTest.class);
      
      String name = builder.create()
         .findType(AnnotationIndexTest.class.getName())
         .getAnnotation(Module.class)
         .value();
      
      assertEquals(name, "blah");
   }

}
