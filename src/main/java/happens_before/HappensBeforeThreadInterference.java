package happens_before;

import thread_interference.Counter;
import thread_interference.CounterDecrementor;
import thread_interference.CounterIncrementor;

public class HappensBeforeThreadInterference {

  public static void main(String[] args) throws InterruptedException {

    int steps = 1000;
    Counter counter = new VolatileCounter();
    Thread t1 = new Thread(new CounterDecrementor(counter, steps));
    Thread t2 = new Thread(new CounterIncrementor(counter, steps));
    t1.start();
    t2.start();

    t1.join();
    t2.join();
    System.out.println("Interference result: " + counter.value());

  }

}
