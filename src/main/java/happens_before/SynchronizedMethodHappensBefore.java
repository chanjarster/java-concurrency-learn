package happens_before;

import java.util.concurrent.TimeUnit;

public class SynchronizedMethodHappensBefore {

  private static boolean stop = false;

  private static synchronized boolean isStop() {
    return stop;
  }

  private static synchronized void markStop() {
    stop = true;
  }

  public static void main(String[] args) throws InterruptedException {

    Thread t1 = new Thread(() -> {

      while (!isStop()) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    markStop();

  }

}
