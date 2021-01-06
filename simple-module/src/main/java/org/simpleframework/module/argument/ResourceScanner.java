package org.simpleframework.module.argument;

import java.net.URL;
import java.util.List;

public interface ResourceScanner {
   List<URL> scan(Iterable<String> sources);
   List<URL> scan(Iterable<String> sources, Iterable<String> extensions);
}
