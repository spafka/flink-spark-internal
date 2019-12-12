package io.github.spafka.checkpoint;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.concurrent.TimeUnit;

public class BaseDemo {

    public static void main(String[] args) throws Exception {


        Configuration configuration = new Configuration();
        //
//        configuration.setBoolean(CheckpointingOptions.ASYNC_SNAPSHOTS,false);
//        configuration.setString(CheckpointingOptions.CHECKPOINTS_DIRECTORY,"file:///D:/checkpoint");


        configuration.setInteger(RestOptions.PORT,8081);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);

        // 每隔10000 ms进行启动一个检查点【设置checkpoint的周期】
        env.enableCheckpointing(10000);

        // 高级选项：
        // 设置模式为exactly-once （这是默认值）
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);

        // 确保检查点之间有至少500 ms的间隔【checkpoint最小间隔】
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);

        // 检查点必须在一分钟内完成，或者被丢弃【checkpoint的超时时间】
        env.getCheckpointConfig().setCheckpointTimeout(60000);

        // 同一时间只允许进行一个检查点
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);

        // 表示一旦Flink处理程序被cancel后，会保留Checkpoint数据，以便根据实际需要恢复到指定的Checkpoint【详细解释见备注】

        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);


        //设置statebackend

        env.setStateBackend(new RocksDBStateBackend("file:///D:\\rock\\flink\\checkpoints",true));

        env.socketTextStream("localhost",9999).flatMap(new FlatMapFunction<String, Tuple2<String, Long>>()  {

            @Override
            public void flatMap(String s, Collector<Tuple2<String, Long>> collector) throws Exception {
                String[] strings = s.split(" ");

                for (String string : strings) {
                    collector.collect(new Tuple2(string,1L));
                }
            }

        }).keyBy(0)
        .sum(1) // .reduce()
        .print();

        env.fromElements("1","2","3","2 3 1").flatMap(new FlatMapFunction<String, Tuple2<String, Long>>()  {

            @Override
            public void flatMap(String s, Collector<Tuple2<String, Long>> collector) throws Exception {
            String[] strings = s.split(" ");

            for (String string : strings) {
                collector.collect(new Tuple2(string,1L));
            }

        }}
        )
               // .slotSharingGroup("source2")
                .keyBy(0)
                .reduce((ReduceFunction<Tuple2<String, Long>>) (old, ne) -> { // 使用的是ValueState

                    System.err.println(new Tuple2(old,ne));
                    TimeUnit.MILLISECONDS.sleep(1);
                    return new Tuple2(old.f0,old.f1+ne.f1);
                }).print();



        env.execute("nc");

    }
}
