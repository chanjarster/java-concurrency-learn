package happens_before;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class ConcurrentCollectionHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    // commonCollectionNeverEnds();
    concurrentCollectionWillEnds();
  }

  private static void concurrentCollectionWillEnds() throws InterruptedException {

    ConcurrentLinkedQueue<Integer> conQueue = new ConcurrentLinkedQueue<>();

    Thread t1 = new Thread(() -> {

      while (!stop) {
        conQueue.peek();
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    stop = true;
    conQueue.add(1);

  }

  private static void commonCollectionNeverEnds() throws InterruptedException {

    Queue<Integer> conQueue = new LinkedList<>();

    Thread t1 = new Thread(() -> {

      while (!stop) {
        conQueue.peek();
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    stop = true;
    conQueue.add(1);

  }

}
