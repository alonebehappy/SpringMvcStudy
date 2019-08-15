// Java线程池的完整构造函数
public ThreadPoolExecutor(
  int corePoolSize, // 正式工数量
  int maximumPoolSize, // 工人数量上限，包括正式工和临时工
  long keepAliveTime, TimeUnit unit, // 临时工游手好闲的最长时间，超过这个时间将被解雇
  BlockingQueue<Runnable> workQueue, // 排期队列
  ThreadFactory threadFactory, // 招人渠道
  RejectedExecutionHandler handler) // 拒单方式
