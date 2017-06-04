package happens_before;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

public class ExchangeHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {
    t1First();
    mainThreadFirst();
  }

  private static void t1First() throws InterruptedException {
    System.out.println("============ t1First ============");

    Exchanger exchanger = new Exchanger();

    Thread t1 = new Thread(() -> {

      String threadName = Thread.currentThread().getName();
      try {
        System.out.println(threadName + " await exchanging");
        System.out.println(threadName + " exchanged: " + exchanger.exchange("from t1"));
      } catch (InterruptedException e) {
        return;
      }

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.SECONDS.sleep(2L);
    stop = true;
    String threadName = Thread.currentThread().getName();
    System.out.println(threadName + " await exchanging");
    System.out.println(threadName + " exchanged: " + exchanger.exchange("from main thread"));

  }

  private static void mainThreadFirst() throws InterruptedException {
    System.out.println("============ mainThreadFirst ============");

    Exchanger exchanger = new Exchanger();

    Thread t1 = new Thread(() -> {

      String threadName = Thread.currentThread().getName();
      try {
        TimeUnit.SECONDS.sleep(2L);
        System.out.println(threadName + " await exchanging");
        System.out.println(threadName + " exchanged: " + exchanger.exchange("from t1"));
      } catch (InterruptedException e) {
        return;
      }

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.SECONDS.sleep(1L);
    stop = true;
    String threadName = Thread.currentThread().getName();
    System.out.println(threadName + " await exchanging");
    System.out.println(threadName + " exchanged: " + exchanger.exchange("from main thread"));

  }

}
