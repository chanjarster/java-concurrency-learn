package future;

import executors.ExecutorsGracefullyShutdown;

import java.util.concurrent.*;

/**
 * Created by qianjia on 2017/5/30.
 */
public class FutureGetTimeout {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    Callable<Integer> task = () -> {
      try {
        TimeUnit.SECONDS.sleep(1);
        return 123;
      } catch (InterruptedException e) {
        throw new IllegalStateException("task interrupted", e);
      }
    };

    ExecutorService executor = Executors.newFixedThreadPool(1);


    try {
      Future<Integer> future = executor.submit(task);

      System.out.println("future done? " + future.isDone());

      Integer result = future.get(1, TimeUnit.SECONDS);
      System.out.println("future done? " + future.isDone());
      System.out.println("result: " + result);
    } catch (TimeoutException e) {
      e.printStackTrace();
    } finally {
      ExecutorsGracefullyShutdown.shutdownAndAwaitTermination(executor);
    }

  }
}
