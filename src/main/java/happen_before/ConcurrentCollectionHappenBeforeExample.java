package happen_before;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * 根据<a href="https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#package.description">javadoc</a>的说法：<br>
 * <quote>Actions in a thread prior to placing an object into any concurrent collection happen-before actions subsequent to the access or removal of that element from the collection in another thread.</quote>
 * Created by qianjia on 2017/6/3.
 */
public class ConcurrentCollectionHappenBeforeExample {

  private static int intVariable = 0;

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    // neverEnds();
    // commonCollectionNeverEnds();
    concurrentCollectionWillEnds();
  }

  /**
   * t1有可能永远都不结束，因为stop变量的变化对其invisible。<br>
   * 见<a href="https://stackoverflow.com/questions/461896/what-is-the-most-frequent-concurrency-issue-youve-encountered-in-java#comment348329_462648">SO的这个comment</a>得到的灵感
   *
   * @throws InterruptedException
   */
  private static void neverEnds() throws InterruptedException {

    Thread t1 = new Thread(() -> {

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    });

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    stop = true;

  }

  /**
   * 利用 Concurrent Collection 的 happen-before 特性，让stop变量的变化对t1 visible
   *
   * @throws InterruptedException
   */
  private static void concurrentCollectionWillEnds() throws InterruptedException {

    ConcurrentLinkedQueue<Integer> conQueue = new ConcurrentLinkedQueue<>();

    Thread t1 = new Thread(() -> {

      while (!stop) {
        conQueue.peek();
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    });

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    stop = true;
    conQueue.add(1);

  }

  /**
   * 普通的 Collection 不具有 happen-before 特性，所以stop变量的变化对于t1 依然是 invisible
   *
   * @throws InterruptedException
   */
  private static void commonCollectionNeverEnds() throws InterruptedException {

    Queue<Integer> conQueue = new LinkedList<>();

    Thread t1 = new Thread(() -> {

      while (!stop) {
        conQueue.peek();
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    });

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    stop = true;
    conQueue.add(1);

  }

}
