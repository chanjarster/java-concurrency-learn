package happens_before;

import thread_interference.Counter;

/**
 * 使用 synchronized 关键字的Counter
 */
public class VolatileCounter implements Counter {

  private volatile int count = 0;

  @Override
  public void increment() {
    count++;
  }

  @Override
  public void decrement() {
    count--;
  }

  @Override
  public int value() {
    return count;
  }

}
