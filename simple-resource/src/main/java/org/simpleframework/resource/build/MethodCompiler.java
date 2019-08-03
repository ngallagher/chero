package org.simpleframework.resource.build;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.module.build.Parameter;
import org.simpleframework.module.extract.StringConverter;
import org.simpleframework.resource.annotation.CONNECT;
import org.simpleframework.resource.annotation.PathParam;

public class MethodCompiler {

   private final PathParser expression;
   private final PathPattern path;
   private final PathIndex index;

   public MethodCompiler(PathPattern path, Parameter... parameters) {
      this.index = new PathIndex(parameters);
      this.expression = new PathParser(path, index);
      this.path = path;
   }

   public MethodMatcher compile(Class<? extends Annotation> verb) {  
      List<String> tokens = expression.names();
      String match = expression.pattern(verb != CONNECT.class);
      String name = verb.getSimpleName();
      String ignore = path.getIgnore();

      return new MethodMatcher(name, ignore, match, tokens, verb != CONNECT.class);
   }
   
   private static class PathIndex {
      
      private final StringConverter converter;
      private final Map<String, Class> types;
      private final Parameter[] parameters;
      
      public PathIndex(Parameter[] parameters) {
         this.types = new HashMap<String, Class>();
         this.converter = new StringConverter();
         this.parameters = parameters;
      }
      
      public Class resolve(String name) {
         if(types.isEmpty()) {
            for(Parameter parameter : parameters) {
               PathParam label = parameter.getAnnotation(PathParam.class);
               
               if(label != null) {
                  String value = label.value();
                  Class type = parameter.getType();
                  Class real = converter.convert(type);
                  
                  types.put(value, real);
               }
            }
         }
         return types.get(name);
      }
   }

   private static class PathParser {

      private List<PathSegment> segments;
      private StringBuilder builder;
      private PathSegment segment;
      private PathIndex index;

      public PathParser(PathPattern path, PathIndex index) {
         this.segments = new ArrayList<PathSegment>();
         this.builder = new StringBuilder();
         this.index = index;
         this.parse(path);
      }

      public List<String> names() {
         List<String> list = new ArrayList<String>();

         for (PathSegment segment : segments) {
            List<String> names = segment.names();
            list.addAll(names);
         }
         return list;
      }

      public String pattern(boolean path) {
         StringBuilder builder = new StringBuilder();

         for (PathSegment segment : segments) {
            String pattern = segment.pattern();
            int length = builder.length();
            
            if(length > 0 || path) {
               builder.append("/");
            }
            builder.append(pattern);
         }
         return builder.toString();
      }

      public void parse(PathPattern path) {
         String[] parts = path.getPaths();
         
         if(parts.length > 0) {
            parse(parts);
         }
      }
      
      public void parse(String[] parts) {
         String path = join(parts);
         char[] data = path.toCharArray();

         for (int i = 0; i < data.length; i++) {
            if (data[i] == '{') {
               createToken(TokenType.NORMAL);

               for (i += 1; i < data.length; i++) {
                  char next = data[i];

                  if (next == '}') {
                     break;
                  }
                  append(next);
               }
               createToken(TokenType.PARAMETER);
            } else if (data[i] == '/') {
               createToken(TokenType.NORMAL);
               startSegment();
            } else {
               append(data[i]);
            }
         }
         createToken(TokenType.NORMAL);
         finishPath();
      }

      private String join(String[] parts) {
         StringBuilder builder = new StringBuilder();

         for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (!part.startsWith("/")) {
               builder.append("/");
            }
            if (i + 1 < parts.length) {
               int length = part.length();

               if (part.endsWith("/")) {
                  part = part.substring(0, length - 1);
               }
            }
            builder.append(part);
         }
         return builder.toString();
      }

      public void append(char ch) {
         builder.append(ch);
      }

      public void append(String text) {
         builder.append(text);
      }

      public void startSegment() {
         if (segment != null) {
            segments.add(segment);
         }
         segment = new PathSegment(index);
      }

      public void createToken(TokenType type) {
         String value = builder.toString();

         if (segment != null) {
            segment.add(type, value);
         }
         builder.setLength(0);
      }

      public void finishPath() {
         if (segment != null) {
            segments.add(segment);
         }
      }
   }

   private static class PathSegment {

      private final List<Token> tokens;
      private final PathIndex index;
      
      public PathSegment(PathIndex index) {
         this.tokens = new ArrayList<Token>();
         this.index = index;
      }

      public List<String> names() {
         List<String> list = new ArrayList<String>();

         for (Token token : tokens) {
            String name = token.name();

            if (name != null) {
               list.add(name);
            }
         }
         return list;
      }

      public String pattern() {
         StringBuilder builder = new StringBuilder();

         for (Token token : tokens) {
            String name = token.name();
            Class type = index.resolve(name);
            String pattern = token.pattern(type);

            builder.append(pattern);
         }
         return builder.toString();
      }

      public void add(TokenType type, String text) {
         Token token = new Token(type, text);

         if (text != null) {
            tokens.add(token);
         }
      }
   }

   private static enum TokenType {
      NORMAL, PARAMETER;
   }

   private static class Token {

      public final TokenType type;
      public final String text;

      public Token(TokenType type, String text) {
         this.type = type;
         this.text = text;
      }

      public String name() {
         if (type == TokenType.PARAMETER) {
            return text;
         }
         return null;
      }

      public String pattern(Class real) {
         if (type == TokenType.PARAMETER) {
            if(real == Double.class) {
               return "([0-9\\.]+)";
            }
            if(real == Float.class) {
               return "([0-9\\.]+)";
            }
            if(real == Integer.class) {
               return "([0-9]+)";
            }
            if(real == Long.class) {
               return "([0-9]+)";
            }
            if(real == Short.class) {
               return "([0-9]+)";
            }
            if(real == Byte.class) {
               return "([0-9]+)";
            }
            return "(.+?)";
         }
         return text;
      }
   }
}
