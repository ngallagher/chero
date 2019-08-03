package org.simpleframework.resource.container;

import org.simpleframework.module.service.Service;

public interface Server extends Service<Acceptor> {
   Server threads(int threads);
   Server name(String name);
   Server session(String cookie);
}
