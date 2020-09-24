package com.dushyant.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dushyantmistry@mac.local
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ObjectLock<T> {
  //<editor-fold desc="Main method with examples">
  /*
  public static void main(String[] args) {
    final Thread runnable1 = new Thread(() -> {
      ObjectLock.executeInNamedLock("110", () -> {
        try {
          System.out.println("running 110");
          TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    });
    final Thread runnable2 = new Thread(() -> {
      ObjectLock.executeInNamedLock("111", () -> {
        try {
          System.out.println("running 111");
          TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    });
    final Thread runnable3 = new Thread(() -> {
      ObjectLock.executeInNamedLock("110", () -> {
        try {
          System.out.println("running 110 2");
          TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    });

    runnable1.start();
    runnable2.start();
    runnable3.start();
  }
  */
  //</editor-fold>

  private final Lock lock = new ReentrantLock();
  private final Map<T, Condition> conditions = new HashMap<>();
  private volatile static ObjectLock<?> INSTANCE;

  private ObjectLock() {
  }

  /**
   * @return thread friendly instance one per jvm session
   */
  public static <T> ObjectLock<T> getInstance() {
    if (INSTANCE == null) {
      synchronized (ObjectLock.class) {
        if (INSTANCE == null) {
          INSTANCE = new ObjectLock<T>();
        }
      }
    }
    //noinspection unchecked
    return (ObjectLock<T>) INSTANCE;
  }

  /**
   * @param t T
   */
  public void lock(T t) {
    lock.lock();
    try {
      while (conditions.containsKey(t)) {
        conditions.get(t).awaitUninterruptibly();
      }
      conditions.put(t, lock.newCondition());
    } finally {
      lock.unlock();
    }
  }

  /**
   * @param userObject to unlock
   */
  public void unlock(T userObject) {
    lock.lock();
    try {
      Condition condition = conditions.get(userObject);
      if (condition == null) {
        throw new IllegalStateException();
      }
      conditions.remove(userObject);
      condition.signalAll();
    } finally {
      lock.unlock();
    }
  }


  /**
   * @param lockName lock name
   * @param runnable runnable
   */
  public static void executeInNamedLock(String lockName, Runnable runnable) {
    synchronized (lockName.intern()) {
      runnable.run();
    }
  }


  /**
   * @param t    t
   * @param time time
   * @param unit unit
   * @return false if couldn't hold lock for given time
   * @throws InterruptedException if interrupted
   */
  public boolean tryLock(T t, long time, TimeUnit unit) throws InterruptedException {
    boolean hold = true;
    lock.lock();
    try {
      while (conditions.containsKey(t)) {
        final boolean await = conditions.get(t).await(time, unit);
        if (!await) {
          hold = false;
          break;
        }
      }
      conditions.put(t, lock.newCondition());
    } finally {
      lock.unlock();
    }
    return hold;
  }
}