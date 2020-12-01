package org.simpleframework.module.core;

public interface TokenFilter {
   void replace(TextBuffer buffer, char[] data, int off, int len);
}