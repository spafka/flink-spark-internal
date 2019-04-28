package org.github.spafka.lock.AQS;

//@see https://www.cnblogs.com/chengxiao/archive/2017/07/24/7141160.html\


/**
 * CLH 队列存放线程,如果没获取到锁，即state状态一般不为0，则把此线程中断 park，并加入到CLH中，直到其前置 head node 任务完成，然后由前置线程任务unlock，
 * release 释放tail node，unpark之
 *
 * <pre>
 *      +------+  prev +-----+       +-----+
 * head |      | <---- |     | <---- |     |  tail
 *      +------+       +-----+       +-----+
 * </pre>
 */
