package com.github.spafka.flip6

import java.util.concurrent.TimeUnit

import org.apache.commons.lang.RandomStringUtils
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.windowing.time.Time

object Simple {

  def main(args: Array[String]): Unit = {

    import org.apache.flink.streaming.api.scala._
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.setParallelism(3)
    val params = ParameterTool.fromArgs(args)
    val windowSize = params.getInt("window", 250)
    val slideSize = params.getInt("slide", 150)
    env.addSource(new SourceFunction[String] {

      @volatile var isRuning = true

      override def run(ctx: SourceFunction.SourceContext[String]): Unit = {

        while (isRuning) {
          ctx.collect(s"${RandomStringUtils.randomNumeric(2)}")

          TimeUnit.MICROSECONDS.sleep(1)
        }
      }

      override def cancel(): Unit = {

        isRuning = false
      }
    }).slotSharingGroup("source").setParallelism(1)

      .flatMap(_.toLowerCase.split("\\W+")).slotSharingGroup("map").setParallelism(1)
      .filter(_.nonEmpty).slotSharingGroup("map").setParallelism(2)
      .map((_, 1)).slotSharingGroup("map").setParallelism(3)
      // group by the tuple field "0" and sum up tuple field "1"
      .keyBy(0)
      .timeWindow(Time.seconds(1))
      .sum(1)
      .print().slotSharingGroup("default")

    env.execute("simple")

  }
}
