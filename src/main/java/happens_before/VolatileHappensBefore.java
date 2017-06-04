package happens_before;

import java.util.concurrent.TimeUnit;

public class VolatileHappensBefore {

  private static volatile boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    Thread t1 = new Thread(() -> {

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    stop = true;

  }

}
