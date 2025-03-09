package com.sinosoft.common.utils;

import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.utils.saveframe.DataCollectionHandler;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * 多线程处理
 *
 * @author: zzf
 * @create: 2023-06-14 23:07
 */
@Slf4j
public class ParserForUtil {
    /**
     * 线程池的大小
     */
    private int threadNum = 6;
    /**
     * 线程池
     */
    private ExecutorService pool;

    /**
     * 线程池的休眠时间
     */
    private int threadSleepTime = 50;
    /**
     * 线程池的计算状态
     */
    private final AtomicBoolean calfalg = new AtomicBoolean(true);
    /**
     *
     */
    private CountDownLatch latch;


    /**
     * 构造函数
     */
    public ParserForUtil() {
        pool = newFixedThreadPool(threadNum);
    }


    /**
     * 构造函数
     *
     * @param threadNum 线程池大小
     */
    public ParserForUtil(int threadNum) {
        this.threadNum = threadNum;
        pool = newFixedThreadPool(threadNum);
    }

    /**
     * 怎么样处理每一条记录
     *
     * @param dh 处理每一条记录的方法
     * @return
     */
    public <T> boolean parserSet(final Collection<T> list, final DataCollectionHandler<T> dh) {
        latch = new CountDownLatch(list.size());
        for (T t : list) {
            pool.execute(() -> {
                try {
                    dh.dataCollectionHanlder(t);
                } catch (Exception e) {
                    calfalg.set(false);
                    // Log the exception using a logging framework
                    log.error("线程池任务执行异常:", e);
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
            System.out.println("当前线程池任务全部执行完毕并释放。");
        } catch (InterruptedException e) {
            log.error("线程池任务执行异常:", e);
            return false;
        }
        if (!pool.isShutdown()) {
            pool.shutdown();
        }
        return calfalg.get();
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
        pool = newFixedThreadPool(this.threadNum);
    }

    public int getThreadSleepTime() {
        return threadSleepTime;
    }

    public void setThreadSleepTime(int threadSleepTime) {
        this.threadSleepTime = threadSleepTime;
    }

    public static void main(String[] args) {
        ParserForUtil parserForUtil = new ParserForUtil(5);
        Set<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < 10; i++) {
            set.add(i);
        }
        List<Integer> listIndex = new ArrayList<>();
        parserForUtil.parserSet(set, (num) -> {
            log.info(num + "---" + Thread.currentThread().getName());
            listIndex.add(num);
        });
        System.out.println(listIndex);
    }
}
