package org.github.spafka.lock.AQS;

import org.github.spafka.util.Utils;
import scala.Tuple2;
import scala.runtime.AbstractFunction0;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;


public class TestMutex {
    private static CyclicBarrier barrier = new CyclicBarrier(31);
    private static int a = 0;
    private static Mutex mutex = new Mutex();
    private static final ReentrantLock lock = new ReentrantLock();

    static final int addPerThread = 1000000;

    public static void main(String[] args) throws Exception {

        Tuple2<Object, Object> tuple2 = Utils.timeTakenMs(new AbstractFunction0<Object>() {
            @Override
            public Object apply() {
                for (int i = 0; i < 5; i++) {
                    Thread t = new Thread(() -> {
                        for (int i12 = 0; i12 < addPerThread; i12++) {
                            increment4();//没有同步措施的a++；
                        }
                        try {
                            barrier.await();//等30个线程累加完毕
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    t.start();
                }
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                return a;
            }
        });


        System.out.println(tuple2);


    }

    public static void increment1() {
        a++;
    }

    public static void increment2() {
       Utils.lock(lock, new AbstractFunction0<Object>() {
           @Override
           public Object apply() {

               a++;
               return null;
           }
       });
    }

    public static void increment3() {
        synchronized (mutex) {
            a++;
        }
    }

    public static void increment4() {

        Utils.lock(mutex, new AbstractFunction0<Object>() {
            @Override
            public Object apply() {
                a++;
                return null;
            }
        });

    }
}


