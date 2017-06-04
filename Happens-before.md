# happens-before

*happens-before*是保证[Memory Visibility][MemoryVisibility]的前提，根据[javadoc][MemoryVisibility]的说明：

> The results of a write by one thread are guaranteed to be 可见 to a read by another thread only if the write operation happens-before the read operation。

这句话的意思说，只有当*happens-before*的时候，一个write operation才对另一个read operation可见。换句话说，就是read operation能够读到write operation的结果。

## 基本的happens-before关系

下面讲解由`synchronized`、`volatile`、`Thread.start()`、`Thread.join()`形成的*happens-before*关系。

### 无happens-before的例子

不利用任何``java.util.concurrent``工具、``synchronized``、`volatile`关键字时，是无法保证*happens-before*机制的。

见[NoHappensBefore.java][NoHappensBefore]的代码：
 
```java
public class NoHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    Thread t1 = new Thread(() -> {

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");
    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    stop = true;

  }

}
```

在上面这个例子里，``t1``有可能永远都不结束，因为``main thread``对``stop``变量修改对于``t1`` 不可见。

### 同thread中的每个action happens-before 后一个action

根据[javadoc][MemoryVisibility]的说明：

> Each action in a thread *happens-before* every action in that thread that comes later in the program's order.

见[InThreadHappensBefore.java][InThreadHappensBefore]的代码：

```java
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
```

这里例子里，``stop = true``和``while (!stop)``是在一同一个thread中的，所以前者对后者visible。

### synchronized 保证 happens-before

根据[javadoc][MemoryVisibility]的说明：

> An `unlock` (`synchronized` block or method exit) of a monitor *happens-before* every subsequent lock (synchronized block or method entry) of that same monitor. And because the happens-before relation is transitive, all actions of a thread prior to unlocking *happen-before* all actions subsequent to any thread locking that monitor.

这里提供了两个例子：

`synchronized`修饰的方法保证*happens-before*的例子：[SynchronizedMethodHappensBefore.java][SynchronizedMethodHappensBefore]

```java
public class SynchronizedMethodHappensBefore {

  private static boolean stop = false;

  private static synchronized boolean isStop() {
    return stop;
  }

  private static synchronized void markStop() {
    stop = true;
  }

  public static void main(String[] args) throws InterruptedException {

    Thread t1 = new Thread(() -> {

      while (!isStop()) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    markStop();

  }

}
```


`synchronized block`保证*happens-before*的例子：[SynchronizedBlockHappensBefore][SynchronizedBlockHappensBefore]

```java
public class SynchronizedBlockHappensBefore {

  private static boolean stop = false;

  private static Object monitor = new Object();

  public static void main(String[] args) throws InterruptedException {

    Thread t1 = new Thread(() -> {
      while (!stop) {
        synchronized (monitor) {
          // 空的，只是为了获得monitor锁
        }
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    synchronized (monitor) {
      stop = true;
    }
  }

}
```

### volatile 保证 happens-before

根据[javadoc][MemoryVisibility]的说明：

> A write to a `volatile` field *happens-before* every subsequent read of that same field. Writes and reads of volatile fields have similar memory consistency effects as entering and exiting monitors, but do not entail mutual exclusion locking.

见[VolatileHappensBefore.java][VolatileHappensBefore]的代码：

```java
public class VolatileHappensBefore {

  private static volatile boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    Thread t1 = new Thread(() -> {

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    stop = true;

  }

}
```

``stop``变量用了`volatile`修饰，因此对``stop``变量的修改对于``t1`` 可见。

### Thread#start happens-before

根据[javadoc][MemoryVisibility]的说明：

> A call to `start` on a thread *happens-before* any action in the started thread.

见[ThreadStartHappensBefore.java][ThreadStartHappensBefore]的代码：

```java
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
```

*happens-before*具有传递性，``stop = true`` *happens-before* ``t1.start()``，``t1.start()`` *happens-before* t1中的所有操作，所以``stop = true`` *happens-before* t1里的所有动作。

### Thread#join happens-before

根据[javadoc][MemoryVisibility]的说明：

> All actions in a thread *happen-before* any other thread successfully returns from a `join` on that thread.

见[ThreadJoinHappensBefore.java][ThreadJoinHappensBefore]的代码：

```java
public class ThreadJoinHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    Thread mainThread = Thread.currentThread();

    Thread t1 = new Thread(() -> {

      try {
        mainThread.join();
      } catch (InterruptedException e) {
        return;
      }

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    stop = true;
    System.out.println(Thread.currentThread().getName() + " finished");

  }
  
}
```
注意上面的例子：`t1`里``mainThread.join()``，当``join``方法返回后，`main thread`的所有操作都对`t1` 可见。

## 扩展的happens-before关系

根据[javadoc][MemoryVisibility]的说明：

> The methods of all classes in java.util.concurrent and its subpackages extend these guarantees to higher-level synchronization.

下面对每一个进行讲解。

### Concurrent collection 保证 happens-before

根据[javadoc][MemoryVisibility]的说明：

> Actions in a thread prior to placing an object into any concurrent collection happen-before actions subsequent to the access or removal of that element from the collection in another thread.

见[ConcurrentCollectionHappensBefore.java][ConcurrentCollectionHappensBefore]的代码：

```java
public class ConcurrentCollectionHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    // commonCollectionNeverEnds();
    concurrentCollectionWillEnds();
  }

  private static void concurrentCollectionWillEnds() throws InterruptedException {

    ConcurrentLinkedQueue<Integer> conQueue = new ConcurrentLinkedQueue<>();

    Thread t1 = new Thread(() -> {

      while (!stop) {
        conQueue.peek();
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    stop = true;
    conQueue.add(1);

  }

  private static void commonCollectionNeverEnds() throws InterruptedException {

    Queue<Integer> conQueue = new LinkedList<>();

    Thread t1 = new Thread(() -> {

      while (!stop) {
        conQueue.peek();
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);
    stop = true;
    conQueue.add(1);

  }

}
```

在上面这个例子中``concurrentCollectionWillEnds``方法利用Concurrent Collection的*happens-before*特性，让``stop``变量的变化对``t1`` 可见。
而普通的Collection不具有*happens-before*特性，所以stop变量的变化对于``t1``依然是invisible。

### Executor happens-before

根据[javadoc][MemoryVisibility]的说明：

> Actions in a thread prior to the submission of a Runnable to an Executor happen-before its execution begins. Similarly for Callables submitted to an ExecutorService.

见[ExecutorSubmissionHappensBefore.java][ExecutorSubmissionHappensBefore]的代码：

```java
public class ExecutorSubmissionHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    ExecutorService executor = Executors.newSingleThreadExecutor();

    stop = true;

    executor.submit(() -> {

      Thread.currentThread().setName("t1");
      
      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    });

    executor.shutdown();

  }

}
```

这个和[ThreadStartHappensBefore.java][ThreadStartHappensBefore]有点类似。

### Future happens-before

根据[javadoc][MemoryVisibility]的说明：

> Actions taken by the asynchronous computation represented by a Future happen-before actions subsequent to the retrieval of the result via Future.get() in another thread.

见[FutureHappensBefore.java][FutureHappensBefore]的代码：

```java
public class FutureHappensBefore {

  private static boolean stop = false;

  public static void main(String[] args) throws InterruptedException {

    ExecutorService executor = Executors.newFixedThreadPool(2);

    Future<?> mainThreadFuture = executor.submit(() -> {
      
      // try ...
      TimeUnit.SECONDS.sleep(5L);
      // catch ...
      
      stop = true;
      System.out.println("main thread future finished");

    });

    executor.submit(() -> {

      Thread.currentThread().setName("t1");

      // try ...
      mainThreadFuture.get();
      // catch ...
      
      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    });

    executor.shutdown();

  }

}
```

在这个例子里`t1`对`mainThreadFuture.get()`，那么`mainThreadFuture`对于``stop``的修改在`Future.get()`返回的时候，对`t1`可见。


### synchronizer对象的释放happens-before获取

根据[javadoc][MemoryVisibility]的说明：

> Actions prior to "releasing" synchronizer methods such as `Lock.unlock`, `Semaphore.release`, and `CountDownLatch.countDown` happen-before actions subsequent to a successful "acquiring" method such as `Lock.lock`, `Semaphore.acquire`, `Condition.await`, and `CountDownLatch.await` on the same synchronizer object in another thread.

也就是说：

1. 在`Lock.unlock`之前的动作，对于`Lock.lock`之后的动作可见
1. 在`Semaphore.release`之前的动作，对于`Semaphore.acquire`之后的动作可见
1. 在`CountDownLatch.countDown`之前的动作，对于`CountDownLatch.await`之后的动作可见

见[LockReleaseHappensBefore.java][LockReleaseHappensBefore]的代码：

```java
public class LockReleaseHappensBefore {

  private static boolean stop = false;

  private static Lock lock = new ReentrantLock();

  public static void main(String[] args) throws InterruptedException {

    Thread t1 = new Thread(() -> {
      while (!stop) {
        lock.lock();
        // 什么都不做，只是为了获得锁
        lock.unlock();
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.MILLISECONDS.sleep(5000L);

    lock.lock();
    stop = true;
    lock.unlock();
  }

}
```

这个例子和[SynchronizedBlockHappensBefore.java][SynchronizedBlockHappensBefore]有点像。

### Exchanger happens-before

根据[javadoc][MemoryVisibility]的说明：

> For each pair of threads that successfully exchange objects via an `Exchanger`, actions prior to the `exchange()` in each thread *happen-before* those subsequent to the corresponding `exchange()` in another thread.

见[ExchangeHappensBefore.java][ExchangeHappensBefore]的代码：

注意``t1First``里面，`t1`先进入``Exchanger.exchange``，`main thread`后进入``Exchange.exchange``：

```java
  private static void t1First() throws InterruptedException {
    System.out.println("============ t1First ============");

    Exchanger exchanger = new Exchanger();

    Thread t1 = new Thread(() -> {

      String threadName = Thread.currentThread().getName();
      try {
        System.out.println(threadName + " await exchanging");
        System.out.println(threadName + " exchanged: " + exchanger.exchange("from t1"));
      } catch (InterruptedException e) {
        return;
      }

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.SECONDS.sleep(2L);
    stop = true;
    String threadName = Thread.currentThread().getName();
    System.out.println(threadName + " await exchanging");
    System.out.println(threadName + " exchanged: " + exchanger.exchange("from main thread"));

  }
```

而``mainThreadFirst``里，`main thread`先进入``Exchange.exchange``，`t1`后进入``Exchanger.exchange``：

```java
  private static void mainThreadFirst() throws InterruptedException {
    System.out.println("============ mainThreadFirst ============");

    Exchanger exchanger = new Exchanger();

    Thread t1 = new Thread(() -> {

      String threadName = Thread.currentThread().getName();
      try {
        TimeUnit.SECONDS.sleep(2L);
        System.out.println(threadName + " await exchanging");
        System.out.println(threadName + " exchanged: " + exchanger.exchange("from t1"));
      } catch (InterruptedException e) {
        return;
      }

      while (!stop) {
      }
      System.out.println(Thread.currentThread().getName() + " finished");
    }, "t1");

    t1.start();

    TimeUnit.SECONDS.sleep(1L);
    stop = true;
    String threadName = Thread.currentThread().getName();
    System.out.println(threadName + " await exchanging");
    System.out.println(threadName + " exchanged: " + exchanger.exchange("from main thread"));

  }
```

从上面可以看到，谁先谁后都不影响`stop`对`t1`可见，所以``Exchanger.exchange()``的*happens-before*具有对称性。

也就是说对于同一个``Exchanger``所涉及到的两个线程，其中任意一个线程中在``exchange()``之前的操作，都对另一个线程中在``exchange()``之后的操作可见。

### CyclicBarrier happens-before

根据[javadoc][MemoryVisibility]的说明：

> Actions prior to calling `CyclicBarrier.await` and `Phaser.awaitAdvance` (as well as its variants) *happen-before* actions performed by the barrier action, and actions performed by the barrier action *happen-before* actions subsequent to a successful return from the corresponding `await` in other threads.

TODO

## 陷阱

### happens-before无法解决Thread interference

需要注意的是，*happens-before*不能解决[Thread interference](Thread-interference.md)的问题。

见[HappensBeforeThreadInterference.java][HappensBeforeThreadInterference]的代码，其内部使用了[VolatileCounter][VolatileCounter]：

```java
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
```

``VolatileCounter#counter``使用了`volatile`修饰，虽然这能解决memory visibility问题，但是依然解决不了[Thread interference](Thread-interference.md)问题，运行的结果很大概率情况下不是0。

### 相关链接

* [javadoc package java.util.concurrent][4]
* [軟體套件 java.util.concurrent 的描述][2]
* [Java official concurrency tutorial - Lesson concurrency][7]
* [Chapter 17 of the Java Language Specification - Happens-before Order][8]

[4]: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#package.description
[2]: https://sites.google.com/site/javacodelibrary/concurrent-api/package-java-util-concurrent
[7]: https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html
[8]: https://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html#jls-17.4.5
[MemoryVisibility]: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#MemoryVisibility
[NoHappensBefore]: src/main/java/happens_before/NoHappensBefore.java
[VolatileHappensBefore]: src/main/java/happens_before/VolatileHappensBefore.java
[ConcurrentCollectionHappensBefore]: src/main/java/happen_before/ConcurrentCollectionHappensBefore.java
[SynchronizedBlockHappensBefore]: src/main/java/happen_before/SynchronizedBlockHappensBefore.java
[SynchronizedMethodHappensBefore]: src/main/java/happen_before/SynchronizedMethodHappensBefore.java
[ThreadStartHappensBefore]: src/main/java/happen_before/ThreadStartHappensBefore.java
[InThreadHappensBefore]: src/main/java/happen_before/InThreadHappensBefore.java
[ThreadJoinHappensBefore]: src/main/java/happen_before/ThreadJoinHappensBefore.java
[HappensBeforeThreadInterference]: src/main/java/happen_before/HappensBeforeThreadInterference.java
[VolatileCounter]: src/main/java/happen_before/VolatileCounter.java
[ExecutorSubmissionHappensBefore]: src/main/java/happen_before/ExecutorSubmissionHappensBefore.java
[FutureHappensBefore]: src/main/java/happen_before/FutureHappensBefore.java
[LockReleaseHappensBefore]: src/main/java/happen_before/LockReleaseHappensBefore.java
[ExchangeHappensBefore]: src/main/java/happen_before/ExchangeHappensBefore.java
