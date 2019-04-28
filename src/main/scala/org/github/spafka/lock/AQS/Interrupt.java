package org.github.spafka.lock.AQS;

import java.io.IOException;
import java.net.ServerSocket;

public class Interrupt {

    public static void main(String args[]) throws Exception {
        Example2 thread = new Example2();
        System.out.println("Starting thread...");
        thread.start();
        Thread.sleep(3000);
        System.out.println("Asking thread to stop...");
        // 发出中断请求
        thread.interrupt();
        Thread.sleep(3000);
        System.out.println("Stopping application...");
    }

    static class Example2 extends Thread {

        public void run() {
            // 每隔一秒检测是否设置了中断标示
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Thread is running...");
                long time = System.currentTimeMillis();
                // 使用while循环模拟 sleep
                while ((System.currentTimeMillis() - time < 1000)) {
                }
            }
            System.out.println("Thread exiting under request...");
        }
    }

    static class Example3 extends Thread {

        public static void main(String args[]) throws Exception {
            Example3 thread = new Example3();
            System.out.println("Starting thread...");
            thread.start();
            Thread.sleep(3000);
            System.out.println("Asking thread to stop...");
            thread.interrupt();// 等中断信号量设置后再调用
            Thread.sleep(3000000);
            System.out.println("Stopping application...");
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Thread running...");
                try {
                    /*
                     * 如果线程阻塞，将不会去检查中断信号量stop变量，所 以thread.interrupt()
                     * 会使阻塞线程从阻塞的地方抛出异常，让阻塞线程从阻塞状态逃离出来，并
                     * 进行异常块进行 相应的处理
                     */
                    Thread.sleep(1000);// 线程阻塞，如果线程收到中断操作信号将抛出异常
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted...");
                    /*
                     * 如果线程在调用 Object.wait()方法，或者该类的 join() 、sleep()方法
                     * 过程中受阻，则其中断状态将被清除
                     */
                    System.out.println(this.isInterrupted());// false

                    //中不中断由自己决定，如果需要真真中断线程，则需要重新设置中断位，如果
                    //不需要，则不用调用
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Thread exiting under request...");
        }


    }

   static class Example4 extends Thread {
        public static void main(String args[]) throws Exception {
            final Object lock1 = new Object();
            final Object lock2 = new Object();
            Thread thread1 = new Thread() {
                public void run() {
                    deathLock(lock1, lock2);
                }
            };
            Thread thread2 = new Thread() {
                public void run() {
                    // 注意，这里在交换了一下位置
                    deathLock(lock2, lock1);
                }
            };
            System.out.println("Starting thread...");
            thread1.start();
            thread2.start();
            Thread.sleep(3000);
            System.out.println("Interrupting thread...");
            thread1.interrupt();
            thread2.interrupt();
            Thread.sleep(3000);
            System.out.println("Stopping application...");
        }

        static void deathLock(Object lock1, Object lock2) {
            try {
                synchronized (lock1) {
                    Thread.sleep(10);// 不会在这里死掉
                    synchronized (lock2) {// 会锁在这里，虽然阻塞了，但不会抛异常
                        System.out.println(Thread.currentThread());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }


   static class Example6 extends Thread {
        volatile ServerSocket socket;

        public static void main(String args[]) throws Exception {
            Example6 thread = new Example6();
            System.out.println("Starting thread...");
            thread.start();
            Thread.sleep(3000);
            System.out.println("Asking thread to stop...");
            Thread.currentThread().interrupt();// 再调用interrupt方法
            thread.socket.close();// 再调用close方法
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            System.out.println("Stopping application...");
        }

        public void run() {
            try {
                socket = new ServerSocket(8888);
            } catch (IOException e) {
                System.out.println("Could not create the socket...");
                return;
            }
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Waiting for connection...");
                try {
                    socket.accept();
                } catch (IOException e) {
                    System.out.println("accept() failed or interrupted...");
                    Thread.currentThread().interrupt();//重新设置中断标示位
                }
            }
            System.out.println("Thread exiting under request...");
        }
    }

}