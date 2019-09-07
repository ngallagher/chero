package org.simpleframework.resource.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.simpleframework.http.Method;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.resource.action.JsonMapper;
import org.simpleframework.resource.action.Operation;
import org.simpleframework.resource.action.Schema;
import org.simpleframework.resource.annotation.Body;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class SchemaGenerator {

   private static final String[] METHODS = {
         Method.GET,
         Method.PUT,
         Method.POST,
         Method.DELETE,
         Method.HEAD     
   };
   
   private final ClassPath path;
   private final Schema schema;
   
   public SchemaGenerator(ClassPath path, Schema schema) {
      this.schema = schema;
      this.path = path;
   }
   
   public String generate() throws Exception {
      Set<ClassNode> entities = new HashSet<>();
      JsonMapper mapper = new JsonMapper();
      PrettyPrinter printer = new DefaultPrettyPrinter();
      
      for(String method : METHODS) {
         Set<Operation> operations = schema.getOperations(method);
         
         for(Operation operation : operations) {
            List<Argument> arguments = operation.getArguments();

            for(Argument argument : arguments) {
               Body body = argument.getAnnotation(Body.class);
               
               if(body != null) {
                  Class type = argument.getType();
                  String name = type.getName();
                  ClassNode node = path.getType(name);
                  
                  entities.add(node);
               }
            }
         }
      }
      return mapper.writer(printer).writeValueAsString(entities);
   }
}
