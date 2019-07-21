package com.roocon.thread.tc2;

public class Demo {
	
	private int a ;
	private int b;
	private int c;
	
	public void a () {
		
		// 写后读
		// 读后写
		// 写后写
		
		b = 2;
		
		a = 1; // 写操作
		c = a; // 读操作
		b = c + a;
		
		System.out.println(b);
	}
	
	public static void main(String[] args) {
		new Demo().a();
	}

}
