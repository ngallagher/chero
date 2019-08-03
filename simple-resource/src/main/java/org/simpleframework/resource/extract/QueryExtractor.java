package org.simpleframework.resource.extract;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.core.Model;
import org.simpleframework.module.extract.StringConverterExtractor;
import org.simpleframework.resource.annotation.QueryParam;

public class QueryExtractor extends StringConverterExtractor {

   public QueryExtractor() {
      super(QueryParam.class);
   }
   
   @Override
   protected List<String> resolve(Argument argument, Context context) {
      QueryParam annotation = argument.getAnnotation(QueryParam.class);
      
      if(annotation != null) {
         Model model = context.getModel();
         Request request = model.get(Request.class);
         
         if(request == null) {
            throw new IllegalStateException("Could not get request from model");
         }
         String name = annotation.value();
         Query query = request.getQuery();    
         String substitute = argument.getDefault();
         List<String> list = query.getAll(name);
         
         if(list != null) {
            if(!list.isEmpty()) {
               return list;
            }
         }
         if(substitute != null) {
            return Arrays.asList(substitute);
         }
      }
      return null;
   }
}
