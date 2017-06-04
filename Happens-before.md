# happens-before

*happens-before*是保证[Memory Visibility][MemoryVisibility]的前提，根据[javadoc][MemoryVisibility]的说明：

> The results of a write by one thread are guaranteed to be visible to a read by another thread only if the write operation happens-before the read operation。

这句话的意思说，只有当*happens-before*的时候，一个write operation才对另一个read operation可见。换句话说，就是read operation能够读到write operation的结果。

需要注意的是，*happens-before*不能解决[Thread interference](Thread-interference.md)的问题。

## 无happens-before的例子

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

在上面这个例子里，``t1``有可能永远都不结束，因为``main thread``对``stop``变量修改对于``t1`` invisible。

## 同thread中的每个action happens-before 后一个action

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

## synchronized 保证 happens-before

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

## volatile 保证 happens-before

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

``stop``变量用了`volatile`修饰，因此对``stop``变量的修改对于``t1`` visible。

## Thread#start happens-before

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

## Thread#join happens-before

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
注意上面的例子：`t1`里``mainThread.join()``，当``join``方法返回后，`main thread`的所有操作都对`t1` visible。

## Concurrent collection 保证 happens-before

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

在上面这个例子中``concurrentCollectionWillEnds``方法利用Concurrent Collection的*happens-before*特性，让``stop``变量的变化对``t1`` visible。
而普通的Collection不具有*happens-before*特性，所以stop变量的变化对于``t1``依然是invisible。


## 相关链接

* [javadoc package java.util.concurrent][4]
* [Java official concurrency tutorial - Lesson concurrency][7]
* [Chapter 17 of the Java Language Specification - Happens-before Order][8]


[4]: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#package.description
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
