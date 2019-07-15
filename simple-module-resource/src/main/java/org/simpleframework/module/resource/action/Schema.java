package org.simpleframework.module.resource.action;

import java.util.Set;

public interface Schema {
   Set<ActionDescription> getActions(String method);
   Object getAttribute(String name);
}
