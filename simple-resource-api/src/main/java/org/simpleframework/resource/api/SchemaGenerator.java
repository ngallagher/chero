package org.simpleframework.resource.api;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.simpleframework.http.Method;
import org.simpleframework.module.build.Argument;
import org.simpleframework.module.path.ClassNode;
import org.simpleframework.module.path.ClassPath;
import org.simpleframework.module.reflect.PropertyAccessor;
import org.simpleframework.resource.action.JsonMapper;
import org.simpleframework.resource.action.Operation;
import org.simpleframework.resource.action.Schema;
import org.simpleframework.resource.annotation.Body;
import org.simpleframework.resource.api.Definition.Property;

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
   
   public void collectPath(BiConsumer<String, Object> consumer) {
      for(String method : METHODS) {
         Set<Operation> operations = schema.getOperations(method);
         
         for(Operation operation : operations) {
            collectPath(consumer, operation);
         }
      }
   }
   
   public void collectPath(BiConsumer<String, Object> consumer, Operation operation) {
      consumer.accept("get", null);
   }
   
   public void collectDefinition(BiConsumer<ClassNode, Definition> consumer) {
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
                  
                  collectDefinition(consumer, node);
               }
            }
         }
      }
   }
   
   public void collectDefinition(BiConsumer<ClassNode, Definition> consumer, ClassNode node) {
      Map<String, Property> map = node.getMethods()
            .stream()
            .filter(method -> method.getParameterTypes().isEmpty())
            .collect(Collectors.toMap(
                  method -> PropertyAccessor.getProperty(method.getName()), 
                  method -> {
                     Property property = new Property();
                     ClassNode result = method.getReturnType();
                     Class type = result.getType();
                     String name = type.getSimpleName();
                     
                     property.setType(name);
                     
                     return property;
                  }));

      Definition definition = new Definition();
      definition.setProperties(map);
      consumer.accept(node, definition);
                 
   }
}
