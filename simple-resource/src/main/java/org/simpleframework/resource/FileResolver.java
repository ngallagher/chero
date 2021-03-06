package org.simpleframework.resource;

import java.io.IOException;

public class FileResolver {

   private final FileManager manager;

   public FileResolver(FileManager manager) {
      this.manager = manager;
   }

   public Content resolveContent(String path) throws IOException {
      return manager.getContent(path);
   }
}