package org.simpleframework.module.resource;

import java.util.List;

import org.simpleframework.module.core.ComponentManager;
import org.simpleframework.module.extract.Extractor;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.resource.action.ActionAssembler;
import org.simpleframework.module.resource.action.Schema;
import org.simpleframework.module.resource.action.write.BodyWriter;

public class ResourceManager {
   
   private final ActionAssembler assembler;
   private final ComponentManager manager;
   
   public ResourceManager(ComponentManager manager, ClassPath path, Schema schema) {
      this.assembler = new ActionAssembler(manager, path, schema);
      this.manager = manager;
   }
   
   public ResourceMatcher create() {
      List<Extractor> extractors = manager.resolveAll(Extractor.class);
      List<BodyWriter> writers = manager.resolveAll(BodyWriter.class);

      return assembler.assemble(extractors, writers);
   }
}
