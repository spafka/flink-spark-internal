package com.github.spafka.rpc

trait StartStopable {
  def start

  def stop
}
