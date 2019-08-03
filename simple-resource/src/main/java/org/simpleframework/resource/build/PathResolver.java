package org.simpleframework.resource.build;

import static org.simpleframework.http.Method.CONNECT;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;

public class PathResolver {

   public String resolve(Context context) throws Exception {
      Model model = context.getModel();
      Request request = model.get(Request.class);
      
      if(request == null) {
         throw new IllegalStateException("Could not get request from model");
      }
      Path path = request.getPath();
      String normalized = path.getPath();
      String method = request.getMethod();
      
      if (method.equals(CONNECT)) { // connect uses domain:port rather than path
         return request.getTarget();
      }
      return normalized;
   }
}
