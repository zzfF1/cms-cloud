package com.sinosoft.common.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.utils.saveframe.DataCollectionHandler;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * @program: ncms
 * @description: 多线程处理
 * @author: zzf
 * @create: 2023-06-14 23:07
 */
@Data
@Slf4j
public class IParserForUtil {
    /**
     * 线程池的大小
     */
    private int threadNum = 12;
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
    private volatile boolean calfalg = true;
    /**
     * 遇到错误 是否继续计算
     */
    private volatile boolean isContinue = false;
    /**
     *
     */
    private CountDownLatch latch;


    /**
     * 构造函数
     */
    public IParserForUtil() {
        pool = newFixedThreadPool(threadNum);
    }


    /**
     * 构造函数
     *
     * @param threadNum 线程池大小
     */
    public IParserForUtil(int threadNum) {
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
                    if (!isContinue && !calfalg) {
                        return;
                    }
                    dh.dataCollectionHanlder(t);
                } catch (Exception e) {
                    calfalg = false;
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
            log.info("当前线程池任务全部执行完毕并释放。");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        if (!pool.isShutdown()) {
            pool.shutdown();
            pool = null;
        }
        return calfalg;
    }

    public static void main(String[] args) {

    }
}
