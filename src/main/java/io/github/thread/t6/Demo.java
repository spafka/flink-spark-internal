package io.github.thread.t6;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class Demo {
	
	
	public synchronized void a () {
		System.out.println("a" +new Date());
		b();
		

	}
	
	public synchronized void b() {
		System.out.println("b" +new Date());
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CountDownLatch countDownLatch = new CountDownLatch(1);
		countDownLatch.countDown();
	}
	
	public static void main(String[] args) {
		Demo d1= new Demo();
		Demo d2= new Demo();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				d1.a();
			}
		}).start();
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				d1.b();
//			}
//		}).start();
	}

}
