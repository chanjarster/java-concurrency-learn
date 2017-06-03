package thread_interference;

/**
 * Counter#decrement
 * Created by qianjia on 2017/6/3.
 */
public class CounterDecrementor implements Runnable {

  private Counter counter;

  private int steps;

  public CounterDecrementor(Counter counter, int steps) {
    this.counter = counter;
    this.steps = steps;
  }

  public void run() {

    int count = steps;
    while (count > 0) {
      count--;
      counter.decrement();
    }

  }

}
