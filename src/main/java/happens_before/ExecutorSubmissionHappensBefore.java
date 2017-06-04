package happens_before;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorSubmissionHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    ExecutorService executor = Executors.newSingleThreadExecutor();

    stop = true;

    executor.submit(() -> {

      Thread.currentThread().setName("t1");

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    });

    executor.shutdown();

  }

}
