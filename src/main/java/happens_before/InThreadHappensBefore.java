package happens_before;

public class InThreadHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    Thread t1 = new Thread(() -> {
      stop = true;
      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

  }
}
