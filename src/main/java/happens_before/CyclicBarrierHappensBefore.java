package happens_before;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException, BrokenBarrierException {

    actionBeforeAwaitHappensBeforeBarrierAction();
//    barrierActionHappensBeforeActionAfterAwait();
  }

  /**
   * Actions prior to calling CyclicBarrier.await and Phaser.awaitAdvance (as well as its variants) happen-before actions performed by the barrier action
   * CyclicBarrier.await 之前的动作 happens-before barrier action
   * @throws BrokenBarrierException
   * @throws InterruptedException
   */
  private static void actionBeforeAwaitHappensBeforeBarrierAction() throws BrokenBarrierException, InterruptedException {

    CyclicBarrier barrier = new CyclicBarrier(1, () -> {
      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    });

    Thread t1 = new Thread(() -> {

      try {
        TimeUnit.SECONDS.sleep(2L);
        stop = true;
        System.out.println(Thread.currentThread().getName() + " await barrier");
        barrier.await();
      } catch (InterruptedException e) {
        return;
      } catch (BrokenBarrierException e) {
        return;
      }

    }, "t1");

    t1.start();


  }

  /**
   * Actions performed by the barrier action happen-before actions subsequent to a successful return from the corresponding await in other threads
   * barrier action happens-before CyclicBarrier.await 返回之后的action
   */
  private static void barrierActionHappensBeforeActionAfterAwait() throws InterruptedException, BrokenBarrierException {

    CyclicBarrier barrier = new CyclicBarrier(1, () -> stop = true);

    Thread t1 = new Thread(() -> {

      String threadName = Thread.currentThread().getName();

      try {
        System.out.println(threadName + " await barrier");
        while (!stop) {
          barrier.await();
        }

      } catch (InterruptedException e) {
        return;
      } catch (BrokenBarrierException e) {
        return;
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

  }

}
