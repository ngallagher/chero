package org.simpleframework.module.argument;

import java.net.URL;
import java.util.List;

public interface ResourceScanner {
   List<URL> scan(Iterable<String> files);
   List<URL> scan(Iterable<String> names, Iterable<String> extensions);
}
