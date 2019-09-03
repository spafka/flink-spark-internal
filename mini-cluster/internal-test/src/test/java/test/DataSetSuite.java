package test;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class DataSetSuite {


    ExecutionEnvironment env;

    @Before
    public void init() {

        env = ExecutionEnvironment.getExecutionEnvironment();
    }


    @Test
    public void map() throws Exception {

       env.readTextFile("random.txt").map(x -> {
            System.out.println(Thread.currentThread().getName() + " -> " + x);
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
                .sum(1).writeAsCsv("van");

        env.execute("van");


    }
}
