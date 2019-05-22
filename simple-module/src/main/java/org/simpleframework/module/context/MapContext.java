package org.simpleframework.module.context;

public class MapContext implements Context {

   private Validation validation;
   private Throwable error;
   private Object result;
   private Model model;

   public MapContext() {
      this.validation = new ContextValidation(this);
      this.model = new MapModel();
   }
   
   @Override
   public Object getResult() {
      return result;
   }

   @Override
   public void setResult(Object result) {
      this.result = result;
   }

   @Override
   public Validation getValidation() {
      return validation;
   }

   @Override
   public void setValidation(Validation validation) {
      this.validation = validation;
   }

   @Override
   public Throwable getError() {
      return error;
   }

   @Override
   public void setError(Throwable error) {
      this.error = error;
   }

   @Override
   public Model getModel() {
      return model;
   }

   @Override
   public void setModel(Model model) {
      this.model = model;
   }
}
