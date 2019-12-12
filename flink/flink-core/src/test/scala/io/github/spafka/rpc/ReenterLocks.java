package io.github.spafka.rpc;

import org.junit.Test;
import scala.runtime.AbstractFunction0;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReenterLocks {


    @Test
    public void _1() {


        ReentrantLock lock = new ReentrantLock();

        new Thread(() -> {
            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }


        }).start();


        new Thread(() -> {
            lock.lock();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }


        }).start();

    }


    @Test
    public void _condition() throws InterruptedException {


        ReentrantLock lock = new ReentrantLock();

        Condition condition = lock.newCondition();
        Condition condition1 = lock.newCondition();

        new Thread(()->{
            LockUtils.lock(lock, new AbstractFunction0<Void>() {
                @Override
                public Void apply() {

                    System.out.println("begin  condition");
                    condition.awaitUninterruptibly();
                    System.out.println("end  condition");

                    return null;
                }
            });


        }).start();



        TimeUnit.SECONDS.sleep(1);

        new Thread(()->{
            LockUtils.lock(lock, new AbstractFunction0<Void>() {
                @Override
                public Void apply() {

                    System.out.println("begin signalAll condition");
                    condition.signalAll();
                    System.out.println("end signalAll condition");

                    return null;
                }
            });


        }).start();

        TimeUnit.SECONDS.sleep(1);




    }
}
