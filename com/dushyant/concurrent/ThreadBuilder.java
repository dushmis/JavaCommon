package com.dushyant.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.function.Supplier;

/**
 * <p>
 * <pre>
 *   new ThreadBuilder()
 *             .setPriority(1)
 *             .setDaemon(false)
 *             .setRunnable(m1::producer)
 *             .build().start();
 *
 *
 *     Executors.newCachedThreadPool(r -> new ThreadBuilder()
 *             .setDaemon(true)
 *             .build());
 *     Executors.newCachedThreadPool(r -> new ThreadBuilder()
 *             .setDaemon(true)
 *             .build((Supplier<Thread>) Thread::new));
 *     Executors.newCachedThreadPool(r -> new ThreadBuilder()
 *             .setName("new-thread")
 *             .setDaemon(true)
 *             .build());
 *
 * </pre>
 * </p>
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ThreadBuilder {
  private boolean daemon = false;
  private UncaughtExceptionHandler uncaughtExceptionHandler;
  private String name;
  private ClassLoader contextClassLoader;
  private int priority;
  private Runnable runnable;

  /**
   * @param daemon daemon or not
   * @return ThreadBuilder
   */
  public ThreadBuilder setDaemon(boolean daemon) {
    this.daemon = daemon;
    return this;
  }

  /**
   * @param uncaughtExceptionHandler UncaughtExceptionHandler
   * @return ThreadBuilder
   */
  public ThreadBuilder setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
    this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    return this;
  }

  /**
   * @param name name of thread
   * @return ThreadBuilder
   */
  public ThreadBuilder setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @param contextClassLoader context class Loader
   * @return ThreadBuilder
   */
  public ThreadBuilder setContextClassLoader(ClassLoader contextClassLoader) {
    this.contextClassLoader = contextClassLoader;
    return this;
  }

  /**
   * @param priority priority
   * @return ThreadBuilder
   */
  public ThreadBuilder setPriority(int priority) {
    this.priority = priority;
    return this;
  }

  /**
   * @param supplier if {@link Supplier} needs to be passed on custom thread creation
   * @return Thread
   */
  public Thread build(Supplier<? extends Thread> supplier) {
    final Thread thread = supplier.get();
    if (this.name != null) {
      thread.setName(this.name);
    }
    thread.setDaemon(this.daemon);
    if (this.contextClassLoader != null) {
      thread.setContextClassLoader(this.contextClassLoader);
    }
    thread.setPriority(this.priority);
    if (this.uncaughtExceptionHandler != null) {
      thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
    }
    return thread;
  }

  /**
   * @param runnable if {@link Supplier} needs to be passed on custom thread creation
   * @return Thread
   */
  public Thread build(Runnable runnable) {
    return build(() -> new Thread(runnable));
  }


  /**
   * @return Thread
   */
  public Thread build() {
    return build(() -> new Thread(this.runnable));
  }
}


