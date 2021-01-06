package org.simpleframework.module.argument;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum AttributeSource {
   INI(BasicReader.class, ".ini"),
   YML(YamlReader.class, ".yml"),
   YAML(YamlReader.class, ".yaml"),
   PROPERTIES(PropertiesReader.class, ".properties"),
   CONF(PropertiesReader.class, ".conf");

   public final Class<? extends AttributeReader> type;
   public final String extension;

   private AttributeSource(Class<? extends AttributeReader> type, String extension) {
      this.extension = extension;
      this.type = type;
   }

   public String extension() {
      return extension;
   }

   public AttributeReader reader() {
      try {
         return type.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
         throw new IllegalStateException("Could not create reader", e);
      }
   }

   public static Set<String> extensions() {
      try {
         AttributeSource[] sources = AttributeSource.values();
         List<AttributeSource> list = Arrays.asList(sources);
         Set<String> extensions = list.stream().map(AttributeSource::extension).collect(Collectors.toSet());

         return Collections.unmodifiableSet(extensions);
      } catch (Exception e) {
         throw new IllegalStateException("Could not resolve extension", e);
      }
   }

   public static AttributeReader reader(URL target) {
      try {
         AttributeSource[] sources = AttributeSource.values();
         String name = target.getFile();

         for (AttributeSource source : sources) {
            if (name.endsWith(source.extension)) {
               return source.reader();
            }
         }
      } catch (Exception e) {
         throw new IllegalStateException("Could not resolve '" + target + "'", e);
      }
      return null;
   }
}

