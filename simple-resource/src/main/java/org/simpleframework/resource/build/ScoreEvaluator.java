package org.simpleframework.resource.build;

import static java.lang.Integer.MIN_VALUE;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.module.build.Function;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;

public class ScoreEvaluator {

   public static final float EXACT_MATCH = 20f;
   public static final float PATTERN_MATCH = 1f;
   public static final float INVALID_MATCH = MIN_VALUE;

   private final ScoreContextBuilder builder;
   private final MethodMatcher matcher;
   private final MethodHeader header;
   private final Function function;

   public ScoreEvaluator(MethodMatcher matcher, MethodHeader header, Function function) {
      this.builder = new ScoreContextBuilder(matcher);
      this.function = function;
      this.matcher = matcher;
      this.header = header;
   }

   public float score(Context context) throws Exception {
      Model model = context.getModel();
      Request request = model.get(Request.class);

      if (request == null) {
         throw new IllegalStateException("Could not get request from model");
      }
      float header = scoreHeader(context);

      if (header >= 0f) {
         Context merged = builder.create(context); // temporary copy for score
         float score = function.getScore(merged);

         if (score >= 0f) {
            return header + score;
         }
      }
      return INVALID_MATCH;
   }

   private float scoreHeader(Context context) throws Exception {
      Model model = context.getModel();
      Request request = model.get(Request.class);

      if (request == null) {
         throw new IllegalStateException("Could not get request from model");
      }
      String method = request.getMethod();
      String verb = matcher.verb();

      if (method.equalsIgnoreCase(verb)) {
         Path path = request.getPath();
         String normal = path.getPath();
         String ignore = matcher.ignore();

         if (ignore.isEmpty() || !normal.matches(ignore)) {
            MethodPattern pattern = matcher.pattern();

            if (!pattern.isExpression()) {
               String literal = pattern.path();

               if (!normal.equals(literal)) {
                  return INVALID_MATCH;
               }
               return EXACT_MATCH + header.score(context);
            }
            return PATTERN_MATCH + header.score(context);
         }
      }
      return INVALID_MATCH;
   }
}