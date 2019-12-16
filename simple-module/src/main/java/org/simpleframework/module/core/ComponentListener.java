package org.simpleframework.module.core;

public interface ComponentListener {
   void onReady();
   default void onDestroy() {}
}
