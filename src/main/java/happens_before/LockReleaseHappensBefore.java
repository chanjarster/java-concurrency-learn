package happens_before;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockReleaseHappensBefore {

  private static boolean stop = false;

  private static Lock lock = new ReentrantLock();

  public static void main(String[] args) throws InterruptedException {

    Thread t1 = new Thread(() -> {
      while (!stop) {
        lock.lock();
        // 什么都不做，只是为了获得锁
        lock.unlock();
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);

    lock.lock();
    stop = true;
    lock.unlock();
  }

}
