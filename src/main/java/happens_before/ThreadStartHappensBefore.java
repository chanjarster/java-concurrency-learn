package happens_before;

public class ThreadStartHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    stop = true;

    Thread t1 = new Thread(() -> {

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

  }
}
