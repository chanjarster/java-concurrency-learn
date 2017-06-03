package executors;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by qianjia on 2017/5/30.
 */
public class ExecutorsInvokeAll {

  public static void main(String[] args) throws InterruptedException {

    ExecutorService executor = Executors.newWorkStealingPool();

    List<Callable<String>> callables = Arrays.asList(
        () -> "task1",
        () -> "task2",
        () -> "task3");

    try {
      executor.invokeAll(callables)
          .stream()
          .map(future -> {
            try {
              return future.get();
            } catch (Exception e) {
              throw new IllegalStateException(e);
            }
          })
          .forEach(System.out::println);
    } finally {

      ExecutorsGracefullyShutdown.shutdownAndAwaitTermination(executor);

    }
  }
}
