package executors;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by qianjia on 2017/5/30.
 */
public class ExecutorsInvokeAny {

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    ExecutorService executor = Executors.newWorkStealingPool();

    List<Callable<String>> callables = Arrays.asList(
        callable("task1", 2),
        callable("task2", 1),
        callable("task3", 3));

    try {

      String result = executor.invokeAny(callables);
      System.out.println(result);

    } finally {

      ExecutorsGracefullyShutdown.shutdownAndAwaitTermination(executor);

    }

  }

  static Callable<String> callable(String result, long sleepSeconds) {
    return () -> {
      TimeUnit.SECONDS.sleep(sleepSeconds);
      return result;
    };
  }
}
