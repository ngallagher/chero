package org.simpleframework.module.core;

public interface Context {   
   Object getResult();
   void setResult(Object result);
   Validation getValidation();
   void setValidation(Validation validation);
   Throwable getError();
   void setError(Throwable cause);
   Model getModel();
   void setModel(Model model);
}
