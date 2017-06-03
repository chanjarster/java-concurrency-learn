package thread_interference;

/**
 * 使用 synchronized 关键字的Counter
 */
public class SynchronizedCounter implements Counter {

  private int count = 0;

  @Override
  public synchronized void increment() {
    count++;
  }

  @Override
  public synchronized void decrement() {
    count--;
  }

  @Override
  public int value() {
    return count;
  }

}
