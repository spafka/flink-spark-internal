package test;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.*;
import org.apache.flink.util.Collector;
import org.junit.Before;
import org.junit.Test;

public class DataSetSuite {


    ExecutionEnvironment env;

    @Before
    public void init() {

        Configuration configuration = new Configuration();
        configuration.setInteger(RestOptions.PORT, 8080);
        configuration.setInteger(TaskManagerOptions.NUM_TASK_SLOTS, 100);

        configuration.setString(NettyShuffleEnvironmentOptions.NETWORK_BUFFERS_MEMORY_MIN, "1m");
        configuration.setInteger(NettyShuffleEnvironmentOptions.NETWORK_NUM_BUFFERS, 4096);
        configuration.setFloat(NettyShuffleEnvironmentOptions.NETWORK_BUFFERS_MEMORY_FRACTION, 0.5f);
        configuration.setString(CoreOptions.TMP_DIRS, "e://flink-tmp");

        env = ExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
    }


    @Test
    public void map() throws Exception {

       env.readTextFile("random.txt").map(x -> {
//            System.out.println(Thread.currentThread().getName() + " -> " + x);
            return x;
        }).flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                // normalize and split the line
                String[] tokens = value.toLowerCase().split("\\W+");

                // emit the pairs
                for (String token : tokens) {
                    if (token.length() > 0) {
                        out.collect(new Tuple2<>(token, 1));
                    }
                }
            }
        }).groupBy(0)
                .sum(1)

                 .writeAsCsv("van.csv").setParallelism(1);

        env.execute("van");


    }
}
