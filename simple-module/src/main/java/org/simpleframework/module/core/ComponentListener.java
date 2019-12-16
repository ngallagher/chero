package org.simpleframework.module.core;

public interface ComponentListener {
   default void onReady() {};
   default void onDestroy() {}
}
