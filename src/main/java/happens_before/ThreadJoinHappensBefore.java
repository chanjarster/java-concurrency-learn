package happens_before;

import java.util.concurrent.TimeUnit;

public class ThreadJoinHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    Thread mainThread = Thread.currentThread();

    Thread t1 = new Thread(() -> {

      try {
        mainThread.join();
      } catch (InterruptedException e) {
        return;
      }

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    stop = true;
    System.out.println(Thread.currentThread().getName() + " finished");

  }

}
