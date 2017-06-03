package thread;

public class ThreadRunExample {

  public static void main(String[] args) {
    Runnable task = () -> {
      String threadName = Thread.currentThread().getName();
      System.out.println("Hello " + threadName);
    };

    Thread thread = new Thread(task);
    thread.start();

    // 并不保证在Hello之后执行
    System.out.println("Done!");

  }
}
