package thread_interference;

/**
 * 不考虑线程安全的Counter
 */
public class SimpleCounter implements Counter {

  private int count = 0;

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
