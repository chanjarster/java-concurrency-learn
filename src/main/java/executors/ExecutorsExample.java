package executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by qianjia on 2017/5/30.
 */
public class ExecutorsExample {

  public static void main(String[] args) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.submit(() -> {
      String threadName = Thread.currentThread().getName();
      System.out.println("Hello " + threadName);
    });

    // 必须shutdown，否则程序不会停止
    executor.shutdown();
  }
}
