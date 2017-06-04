# Thread Interference

根据[Java官方教程-Thread interference][official-tutorial-thread-interference]的例子，``Counter#c``的结果很有可能不是0。这里提供[ThreadInterference.java][ThreadInterference]来重现这种情况。

``ThreadInterference#interference``方法使用了[SimpleCounter][SimpleCounter]，也就是没有采用任何并发工具的Counter，其执行完毕后的结果很有可能不是0：

```java
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
```

``ThreadInterference#noInterference``方法使用了[SynchronizedCounter][SynchronizedCounter]，它执行完毕后的结果肯定是0：

```java
public class SynchronizedCounter implements Counter {

  private int count = 0;

  @Override
  public synchronized void increment() {
    count++;
  }

  @Override
  public synchronized void decrement() {
    count--;
  }

  @Override
  public int value() {
    return count;
  }

}
```


## 相关链接

* [Java official concurrency tutorial - Lesson concurrency][7]


[1]: http://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/
[2]: http://winterbe.com/posts/2015/04/30/java8-concurrency-tutorial-synchronized-locks-examples/
[3]: http://winterbe.com/posts/2015/05/22/java8-concurrency-tutorial-atomic-concurrent-map-examples/
[4]: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#package.description
[5]: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html
[6]: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledExecutorService.html
[7]: https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html
[8]: https://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html#jls-17.4.5
[official-tutorial-thread-interference]: https://docs.oracle.com/javase/tutorial/essential/concurrency/interfere.html
[ThreadInterference]: src/main/java/thread_interference/ThreadInterference.java
[MemoryVisibility]: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#MemoryVisibility
[NoHappensBefore]: src/main/java/happens_before/NoHappensBefore.java
[VolatileHappensBefore]: src/main/java/happens_before/VolatileHappensBefore.java
[ConcurrentCollectionHappensBefore]: src/main/java/happen_before/ConcurrentCollectionHappensBefore.java
[SynchronizedBlockHappensBefore]: src/main/java/happen_before/SynchronizedBlockHappensBefore.java
[SynchronizedMethodHappensBefore]: src/main/java/happen_before/SynchronizedMethodHappensBefore.java
[SimpleCounter]: src/main/java/thread_interference/SimpleCounter.java
[SynchronizedCounter]: src/main/java/thread_interference/SynchronizedCounter.java
[ThreadStartHappensBefore]: src/main/java/happen_before/ThreadStartHappensBefore.java
[InThreadHappensBefore]: src/main/java/happen_before/InThreadHappensBefore.java
