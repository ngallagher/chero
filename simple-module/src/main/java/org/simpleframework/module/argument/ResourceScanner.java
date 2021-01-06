package org.simpleframework.module.argument;

import java.net.URL;
import java.util.List;
import java.util.Set;

public interface ResourceScanner {
   List<URL> scan(Set<String> sources);
   List<URL> scan(Set<String> sources, Set<String> extensions);
}
