package thread_interference;

/**
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/interfere.html
 * Created by qianjia on 2017/6/3.
 */
public class ThreadInterferenceExample {

  public static void main(String[] args) throws InterruptedException {

    threadNotSafe();
    threadSafe();
  }

  private static void threadNotSafe() throws InterruptedException {

    int steps = 1000;
    Counter counter = new SimpleCounter();
    Thread t1 = new Thread(new CounterDecrementor(counter, steps));
    Thread t2 = new Thread(new CounterIncrementor(counter, steps));
    t1.start();
    t2.start();

    t1.join();
    t2.join();
    System.out.println("Thread not safe result: " + counter.value());

  }

  private static void threadSafe() throws InterruptedException {
    int steps = 1000;
    Counter counter = new SynchronizedCounter();
    Thread t1 = new Thread(new CounterDecrementor(counter, steps));
    Thread t2 = new Thread(new CounterIncrementor(counter, steps));
    t1.start();
    t2.start();

    t1.join();
    t2.join();
    System.out.println("Thread safe result: " + counter.value());
  }
}
