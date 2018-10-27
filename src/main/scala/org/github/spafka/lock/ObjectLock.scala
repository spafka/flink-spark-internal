package org.github.spafka.lock

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.LockSupport

import org.github.spafka.util.Utils
import org.junit.Test

class Lock {

  @Test
  def objectLock = {
    val o = new Object
    new Thread(new Runnable {
      override def run(): Unit = {
        o.synchronized {
          println("brefore await")
          o.wait()
        }
        println("object 获取锁")
      }
    }).start()

    Utils.time({
      o.synchronized {
        println("unlocked")
        o.notifyAll()
      }
    })
  }


  // locksupport 提供了线程级别的中断，并且不受先中断，还是先释放的影响
  @Test
  def lockSupport: Unit = {
    val run = new Thread(new Runnable {
      override def run(): Unit = {

        println(s"${System.currentTimeMillis()}")
        TimeUnit.SECONDS.sleep(3)
        println(s"${System.currentTimeMillis()}")
        LockSupport.park()
      }
    })

    run.start()
    LockSupport.unpark(run)
    Thread.sleep(5000)

  }
}
