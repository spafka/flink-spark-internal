package io.github.spafka.concurrent.lock;

import org.github.spafka.util.Utils;
import scala.runtime.AbstractFunction0;

import java.util.concurrent.TimeUnit;

public class ObjectLock {


    public static void main(String[] args) throws InterruptedException {


        Object lock = new Object();

        new Thread(() -> {

            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("object 获取锁");
            }


        }).start();


        AbstractFunction0<Object> function0 = new AbstractFunction0<Object>() {
            @Override
            public Object apply() {
                synchronized (lock) {
                    System.out.println("lock 释放锁");
                    lock.notify();
                }
                return null;
            }
        };

        System.out.println(Utils.time(function0));


    }
}
