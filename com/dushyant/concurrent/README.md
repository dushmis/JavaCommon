> traditional approach
```
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
  ```

> another example with ObjectLock


```
  public static void main(String[] args) {
    String user1 = "user1@google.com";
    String user2 = "user2@google.com";


    //operation on user1 will lock other operations on user1, while operations on user2 will be proceed and won't get blocked.
    final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    executorService.submit(() -> {
      ObjectLock.<String>getInstance().lock(user1);
      System.out.println("user1 in process = " + user1);
      Util.sleep(user1, 10);//sleep 10 seconds
      ObjectLock.<String>getInstance().unlock(user1);
    });
    executorService.submit(() -> {
      ObjectLock.<String>getInstance().lock(user1);
      System.out.println("user1 in process = " + user1);
      Util.sleep(user1, 10);//sleep 10 seconds
      ObjectLock.<String>getInstance().unlock(user1);
    });
    executorService.submit(() -> {
      ObjectLock.<String>getInstance().lock(user2);
      System.out.println("user2 in process = " + user2);
      Util.sleep(user1, 10);//sleep 10 seconds
      ObjectLock.<String>getInstance().unlock(user2);
    });
    executorService.submit(() -> {
      ObjectLock.<String>getInstance().lock(user2);
      System.out.println("user2 in process = " + user2);
      Util.sleep(user1, 10);//sleep 10 seconds
      ObjectLock.<String>getInstance().unlock(user2);
    });
    executorService.shutdown();
    try {
      System.out.println("waiting");
      final boolean b = executorService.awaitTermination(10, TimeUnit.SECONDS);
      if (!b) {
//        we can cancel task if we want to here
//        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  ```