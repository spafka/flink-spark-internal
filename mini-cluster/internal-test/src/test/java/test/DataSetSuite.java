package test;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.*;
import org.apache.flink.util.Collector;
import org.apache.spark.util.Utils;
import org.junit.Before;
import org.junit.Test;
import scala.runtime.AbstractFunction0;

import static org.apache.flink.configuration.ConfigConstants.LOCAL_NUMBER_TASK_MANAGER;

public class DataSetSuite {


    ExecutionEnvironment env;

    @Before
    public void init() {

        Configuration configuration = new Configuration();
        configuration.setInteger(RestOptions.PORT, 8080);
        configuration.setInteger(TaskManagerOptions.NUM_TASK_SLOTS, 10);
        configuration.setInteger(LOCAL_NUMBER_TASK_MANAGER,2);

        configuration.setString(NettyShuffleEnvironmentOptions.NETWORK_BUFFERS_MEMORY_MIN, "1m");
        configuration.setInteger(NettyShuffleEnvironmentOptions.NETWORK_NUM_BUFFERS, 4096);
        configuration.setFloat(NettyShuffleEnvironmentOptions.NETWORK_BUFFERS_MEMORY_FRACTION, 0.5f);
        configuration.setString(CoreOptions.TMP_DIRS, "e://flink-tmp");

        env = ExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
    }


    @Test
    public void count() throws Exception {

        scala.Tuple2<Long, Object> timeTakenMs = Utils.timeTakenMs(new AbstractFunction0<Long>() {
            @Override
            public Long apply() {

                try {
                    long count = env
                            .readTextFile("random.txt") // DataSource
                            .count(); // DataSink

                    return count;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return 0L;

            }
        });

        System.err.println(timeTakenMs);

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
