package com.github.spafka.concurrent.future;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.*;

public class JFuture {


    @Test
    public void base() {

        System.out.println("start at " + new Date());
        FutureTask<Date> task = new FutureTask<>(() -> {
            System.out.println(Thread.currentThread().getName() + " -> " + new Date());
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName() + " -> " + new Date());
            return new Date();
        });
        new Thread(task).start();

        Date date = null;
        try {
            date = task.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println("end " + date);
    }

    @Test
    public void completeFuture() throws ExecutionException, InterruptedException {

        useCompletableFuture();

        TimeUnit.SECONDS.sleep(1);
    }

    private static void useCompletableFuture() throws InterruptedException, ExecutionException {
        System.out.println("CompletableFuture");

        CompletableFuture.supplyAsync(() -> "A").thenAcceptAsync(name -> {
            System.out.println(name);
        });

    }


}
