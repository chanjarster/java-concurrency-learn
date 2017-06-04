package happens_before;

import java.util.concurrent.TimeUnit;

public class SynchronizedBlockHappensBefore {

  private static boolean stop = false;

  private static Object monitor = new Object();

  public static void main(String[] args) throws InterruptedException {

    Thread t1 = new Thread(() -> {
      while (!stop) {
        synchronized (monitor) {
          // 空的，只是为了获得monitor锁
        }
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    synchronized (monitor) {
      stop = true;
    }
  }

}
