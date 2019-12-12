package io.github.spafka.rpc

import java.util.concurrent.locks.Lock

object LockUtils {


  def lock[T](l: Lock, body: => T) = {
    try {
      l.lock()
      body
    }
    finally {
      l.unlock()
    }
  }
}
