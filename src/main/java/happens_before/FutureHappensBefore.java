package happens_before;

import java.util.concurrent.*;

public class FutureHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    ExecutorService executor = Executors.newFixedThreadPool(2);

    Future<?> mainThreadFuture = executor.submit(() -> {
      try {
        TimeUnit.SECONDS.sleep(5L);
      } catch (InterruptedException e) {
        return;
      }
      stop = true;
      System.out.println("main thread future finished");

    });

    executor.submit(() -> {

      Thread.currentThread().setName("t1");

      try {
        mainThreadFuture.get();
      } catch (InterruptedException e) {
        return;
      } catch (ExecutionException e) {
        return;
      }

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    });

    executor.shutdown();

  }

}
