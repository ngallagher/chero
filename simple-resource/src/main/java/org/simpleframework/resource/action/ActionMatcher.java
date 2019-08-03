package org.simpleframework.resource.action;

import java.util.Set;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Validation;
import org.simpleframework.resource.Resource;
import org.simpleframework.resource.ResourceMatcher;
import org.simpleframework.resource.write.ResponseWriter;

public class ActionMatcher implements ResourceMatcher {

   private final RequestContextBuilder builder;
   private final ActionResolver resolver;
   private final ResponseWriter router;

   public ActionMatcher(ActionResolver resolver, ResponseWriter router) {
      this.builder = new RequestContextBuilder();
      this.resolver = resolver;
      this.router = router;
   }

   @Override
   public Resource match(Request request, Response response) throws Exception {
      Context context = builder.build(request, response);
      Action action = resolver.resolve(context);

      if (action != null) {
         Validation validation = context.getValidation();
         Set<String> errors = validation.getErrors();
         
         if(errors != null) {
            errors.clear();
         }
         return new ActionResource(router, action, context);
      }
      return null;
   }
}
