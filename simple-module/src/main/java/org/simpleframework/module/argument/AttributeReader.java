package org.simpleframework.module.argument;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public interface AttributeReader {
   
   default Map<String, String> read(Set<String> paths, String extension) {
      for(String path : paths) {
         Path file = Paths.get(path + extension);
         
         if(Files.isRegularFile(file)) {
            try {
               Reader reader = Files.newBufferedReader(file);
               
               try {
                  return read(reader);
               } finally {
                  reader.close();
               }
            } catch(Exception e) {
               throw new IllegalArgumentException("Could not read " + file, e);
            }
         }
      }
      return Collections.emptyMap();
   }
   
   default boolean exists(Set<String> paths, String extension) {
      for(String path : paths) {
         Path file = Paths.get(path + extension);
         
         if(Files.isRegularFile(file)) {
            return true;
         }
      }
      return false;
   }
   
   Map<String, String> read(Reader reader);
}
