package com.github.spafka

import java.lang.reflect.{InvocationHandler, Method, Proxy}

object ProxyTesting {

  class ProxyInvocationHandler extends InvocationHandler {
    def invoke(proxy: scala.AnyRef,
               method: Method,
               args: Array[AnyRef]): AnyRef = {
      println(
        "Hello Stackoverflow when invoking method with name \"%s\""
          .format(method.getName)
      )
      proxy
    }
  }

  trait Iface {
    def doNothing()
  }

  def main(args: Array[String]) {
    val handler = new ProxyInvocationHandler

    val impl: Iface = Proxy
      .newProxyInstance(
        classOf[Iface].getClassLoader,
        Array(classOf[Iface]),
        handler
      )
      .asInstanceOf[Iface]

    impl.doNothing()
  }

}
