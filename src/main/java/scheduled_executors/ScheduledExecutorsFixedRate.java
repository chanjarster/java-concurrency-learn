package scheduled_executors;

import executors.ExecutorsGracefullyShutdown;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by qianjia on 2017/5/30.
 */
public class ScheduledExecutorsFixedRate {

  public static void main(String[] args) throws InterruptedException {

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    try {
      Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());

      int initialDelay = 0;
      int period = 1;
      // Fixed Rate按照固定频率开始执行，不管任务实际需要执行的时长，因此如果任务执行时间长，任务的执行会重叠
      executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);

    } finally {
      ExecutorsGracefullyShutdown.shutdownAndAwaitTermination(executor);
    }

  }

}
