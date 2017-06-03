package executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by qianjia on 2017/5/30.
 */
public class ExecutorsGracefullyShutdown {

  public static void main(String[] args) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(() -> {
      String threadName = Thread.currentThread().getName();
      System.out.println("Hello " + threadName);
      try {
        // 模拟执行超时的情况
        TimeUnit.SECONDS.sleep(10L);
      } catch (InterruptedException e) {
        System.out.println(threadName + " interrupted");
      }
    });

    shutdownAndAwaitTermination(executor);

  }

  /**
   * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html
   * @param executorService
   */
  public static void shutdownAndAwaitTermination(ExecutorService executorService) {

    // Disable new tasks from being submitted
    executorService.shutdown();

    try {

      // Wait a while for existing tasks to terminate
      if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {

        System.out.println("Shutdown attempt failed");

        // Cancel currently executing tasks
        executorService.shutdownNow();

        // Wait a while for tasks to respond to being cancelled
        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {

          System.out.println("ShutdownNow attempt failed");

        } else {

          System.out.println("ShutdownNow attempt success");

        }
      } else {

        System.out.println("Shutdown attempt success");

      }
    } catch (InterruptedException ie) {

      System.err.println("main thread interrupted");
      // (Re-)Cancel if current thread also interrupted
      executorService.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();

    }
  }

}
