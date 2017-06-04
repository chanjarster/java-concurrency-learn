package thread_interference;

public class ThreadInterference {

  public static void main(String[] args) throws InterruptedException {

    interference();
    noInterference();
  }

  private static void interference() throws InterruptedException {

    int steps = 1000;
    Counter counter = new SimpleCounter();
    Thread t1 = new Thread(new CounterDecrementor(counter, steps));
    Thread t2 = new Thread(new CounterIncrementor(counter, steps));
    t1.start();
    t2.start();

    t1.join();
    t2.join();
    System.out.println("Interference result: " + counter.value());

  }

  private static void noInterference() throws InterruptedException {
    int steps = 1000;
    Counter counter = new SynchronizedCounter();
    Thread t1 = new Thread(new CounterDecrementor(counter, steps));
    Thread t2 = new Thread(new CounterIncrementor(counter, steps));
    t1.start();
    t2.start();

    t1.join();
    t2.join();
    System.out.println("No interference result: " + counter.value());
  }
}
