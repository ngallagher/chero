package org.simpleframework.module.argument;

public enum AttributeSource {
   INI(BasicReader.class, ".ini"),
   YML(YamlReader.class, ".yml"),
   YAML(YamlReader.class, ".yaml"),
   PROPERTIES(PropertiesReader.class, ".properties"),
   CONF(PropertiesReader.class, ".conf");
   
   private final Class<? extends AttributeReader> type;
   private final String extension;
   
   private AttributeSource(Class<? extends AttributeReader> type, String extension) {
      this.extension = extension;
      this.type = type;
   }
   
   public AttributeReader reader() {
      try {
         return type.getDeclaredConstructor(String.class).newInstance(extension);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create reader", e);
      }
   }
}
