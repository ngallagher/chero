package org.simpleframework.module.resource.action.extract;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.module.build.Parameter;
import org.simpleframework.module.context.Context;
import org.simpleframework.module.context.Model;
import org.simpleframework.module.extract.StringConverterExtractor;
import org.simpleframework.module.resource.annotation.QueryParam;

public class QueryExtractor extends StringConverterExtractor {

   public QueryExtractor() {
      super(QueryParam.class);
   }
   
   @Override
   protected List<String> resolve(Parameter parameter, Context context) {
      QueryParam annotation = parameter.getAnnotation(QueryParam.class);
      
      if(annotation != null) {
         Model model = context.getModel();
         Request request = model.get(Request.class);
         
         if(request == null) {
            throw new IllegalStateException("Could not get request from model");
         }
         String name = annotation.value();
         Query query = request.getQuery();    
         String substitute = parameter.getDefault();
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
