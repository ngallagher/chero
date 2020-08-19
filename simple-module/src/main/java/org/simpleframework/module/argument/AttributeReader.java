package org.simpleframework.module.argument;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

public interface AttributeReader {

   default Map<String, String> read(URL target) {
      try {
         InputStream source = target.openStream();

         try {
            return read(source);
         } finally {
            source.close();
         }
      } catch (Exception e) {
         throw new IllegalArgumentException("Could not read " + target, e);
      }
   }

   default Map<String, String> read(InputStream source) {
      try {
         InputStreamReader reader = new InputStreamReader(source, UTF_8);

         try {
            return read(reader);
         } finally {
            source.close();
         }
      } catch (Exception e) {
         throw new IllegalArgumentException("Could not read UTF-8 stream", e);
      }
   }

   Map<String, String> read(Reader source);
}
