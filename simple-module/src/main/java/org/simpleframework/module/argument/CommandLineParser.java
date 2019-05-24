package org.simpleframework.module.argument;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommandLineParser {
   
   private final String prefix;
   
   public CommandLineParser() {
      this("--");
   }
   
   public CommandLineParser(String prefix) {
      this.prefix = prefix;
   }

   public Map<String, String> parse(String[] arguments) {
      Map<String, String> map = new LinkedHashMap<>();
   
      for(String argument : arguments) {
         if(argument.startsWith(prefix)) {
            String command = argument.substring(2);
            String[] pair = command.split("=");
            String key = pair[0];
            String value = pair[1];
            
            if(value == null) {
               throw new IllegalArgumentException("Argument '" + argument + "' has no value");
            }
            int length = value.length();
            
            if(length > 1) {
               String start = value.substring(0, 1);
               
               if(start.equals("\"") || start.equals("\'")) {
                  if(value.endsWith(start)) {
                     value = value.substring(1, length - 1);
                  }
               }
            }
            map.put(key, value);
         }
      }
      return map;
   }
}
