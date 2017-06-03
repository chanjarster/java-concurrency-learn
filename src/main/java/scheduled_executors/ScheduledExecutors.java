package scheduled_executors;

import executors.ExecutorsGracefullyShutdown;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by qianjia on 2017/5/30.
 */
public class ScheduledExecutors {

  public static void main(String[] args) throws InterruptedException {

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    try {
      Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());
      ScheduledFuture<?> future = executor.schedule(task, 3, TimeUnit.SECONDS);

      TimeUnit.MILLISECONDS.sleep(1337);


      long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
      System.out.printf("Remaining Delay: %sms\n", remainingDelay);

    } finally {
      ExecutorsGracefullyShutdown.shutdownAndAwaitTermination(executor);
    }

  }

}
