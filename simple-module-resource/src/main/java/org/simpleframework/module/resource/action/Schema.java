package org.simpleframework.module.resource.action;

import java.util.Set;

public interface Schema {
   Set<Operation> getOperations(String method);
   Object getAttribute(String name);
}
