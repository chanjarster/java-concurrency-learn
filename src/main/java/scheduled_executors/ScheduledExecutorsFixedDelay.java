package scheduled_executors;

import executors.ExecutorsGracefullyShutdown;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by qianjia on 2017/5/30.
 */
public class ScheduledExecutorsFixedDelay {

  public static void main(String[] args) throws InterruptedException {

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    try {
      Runnable task = () -> {
        try {
          TimeUnit.SECONDS.sleep(2);
          System.out.println("Scheduling: " + System.nanoTime());
        }
        catch (InterruptedException e) {
          System.err.println("task interrupted");
        }
      };

      // Fixed Delay 前一次执行结束后必须等待X时间才会进行下一次执行，不会像Fixed Rate那样重叠
      executor.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS);

    } finally {
      ExecutorsGracefullyShutdown.shutdownAndAwaitTermination(executor);
    }

  }

}
