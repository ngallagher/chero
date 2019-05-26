package org.simpleframework.module.resource;

import java.util.Set;

import org.simpleframework.module.build.Service;
import org.simpleframework.module.core.Context;
import org.simpleframework.module.graph.ClassPath;

public class ServerLauncher {
   
   private final ClassPath path;
   private final Context context;
   private final Set<Class> ignore;

   public ServerLauncher(ClassPath path, Context context, Set<Class> ignore) {
      this.ignore = ignore;
      this.context = context;
      this.path = path;
   }  
}
