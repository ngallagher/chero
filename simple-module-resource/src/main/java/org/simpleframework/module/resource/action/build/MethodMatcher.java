package org.simpleframework.module.resource.action.build;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodMatcher {
   
   private final List<String> tokens;
   private final Pattern pattern;
   private final String ignore;
   private final String match;
   private final String verb;
   
   public MethodMatcher(String verb, String ignore, String match, List<String> tokens, boolean path) {
      this.tokens = Collections.unmodifiableList(tokens);
      this.pattern = Pattern.compile(match);
      this.ignore = ignore;
      this.match = match;
      this.verb = verb;
   }

   public Map<String, String> evaluate(String path) {
      if(!tokens.isEmpty()) {
         Map<String, String> parameters = new LinkedHashMap<String, String>();
   
         if (!path.isEmpty()) {
            Matcher matcher = pattern.matcher(path);
   
            if (matcher.matches()) {
               int groups = matcher.groupCount();
               int required = tokens.size();
   
               if (groups < required) {
                  throw new IllegalStateException("Could not extract parameters from " + path);
               }
               for (int i = 0; i < required; i++) {
                  String name = tokens.get(i);
                  String token = matcher.group(i + 1);
   
                  parameters.put(name, token);
               }
            }
         }
         return Collections.unmodifiableMap(parameters);
      }
      return Collections.emptyMap();
   }
   
   public String pattern() {
      return match;
   }
   
   public String verb() {
      return verb;
   }
   
   public String ignore() {
      return ignore;
   }
}
