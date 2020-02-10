package io.github.spafka.processfunction;

import io.github.spafka.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.windowing.RichWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import static java.util.concurrent.TimeUnit.SECONDS;


public class GroupedProcessingTimeWindowExample {

    public static void main(String[] args) throws Exception {


        long start = System.currentTimeMillis();
        final StreamExecutionEnvironment env = Utils.getStreamEnv();
        env.setParallelism(4);
        //env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<Tuple2<Long, Long>> stream = env.addSource(new DataSource());

        stream
//                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Tuple2<Long, Long>>(Time.of(0, MILLISECONDS)) {
//                    @Override
//                    public long extractTimestamp(Tuple2<Long, Long> element) {
//                        return System.currentTimeMillis() - new Random().nextInt(1000* 3);
//                    }
//                })
                .keyBy(0)
                .timeWindow(Time.of(5, SECONDS))
                .reduce(new SummingReducer(), new PassThroughWindowChechPointFunction())
                .addSink(new SinkFunction<Tuple2<Long, Long>>() {
                    @Override
                    public void invoke(Tuple2<Long, Long> value) {
                        System.out.println("当前窗口，" + value);
                    }
                });

        env.execute();

        System.out.println(System.currentTimeMillis() - start);
    }


    @Slf4j
    private static class PassThroughWindowChechPointFunction extends RichWindowFunction<Tuple2<Long, Long>, Tuple2<Long, Long>, Tuple, TimeWindow> implements CheckpointedFunction {

        private ReducingState<Tuple2<Long, Long>> countPerKey;

        private Tuple2<Long, Long> localCount;

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            //countPerPartition.clear();

        }


        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {

            // get the state data structure for the per-key state
            countPerKey = context.getKeyedStateStore().getReducingState(
                    new ReducingStateDescriptor<Tuple2<Long, Long>>("perKeyCount", new ReduceFunction<Tuple2<Long, Long>>() {
                        @Override
                        public Tuple2<Long, Long> reduce(Tuple2<Long, Long> value1, Tuple2<Long, Long> value2) throws Exception {
                            return Tuple2.of(value1.f0, value1.f1 + value2.f1);
                        }
                    }, TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {
                    })));

            // get the state data structure for the per-partition state

        }


        @Override
        public void apply(Tuple tuple, TimeWindow window, Iterable<Tuple2<Long, Long>> input, Collector<Tuple2<Long, Long>> out) throws Exception {

            for (Tuple2<Long, Long> x : input) {
                countPerKey.add(x);
                out.collect(x);

            }


            System.err.println("key = " + tuple + "value = " + countPerKey.get());

        }
    }

    private static class FirstFieldKeyExtractor<Type extends Tuple, Key> implements KeySelector<Type, Key> {

        @Override
        @SuppressWarnings("unchecked")
        public Key getKey(Type value) {
            return (Key) value.getField(0);
        }
    }

    private static class SummingWindowFunction implements WindowFunction<Tuple2<Long, Long>, Tuple2<Long, Long>, Long, TimeWindow> {

        @Override
        public void apply(Long key, TimeWindow window, Iterable<Tuple2<Long, Long>> values, Collector<Tuple2<Long, Long>> out) {
            long sum = 0L;
            for (Tuple2<Long, Long> value : values) {
                sum += value.f1;
            }

            out.collect(new Tuple2<>(key, sum));
        }
    }

    private static class SummingReducer implements ReduceFunction<Tuple2<Long, Long>>, CheckpointedFunction {

        @Override
        public Tuple2<Long, Long> reduce(Tuple2<Long, Long> value1, Tuple2<Long, Long> value2) {
            return new Tuple2<>(value1.f0, value1.f1 + value2.f1);
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {

            throw new UnsupportedOperationException();
        }

        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            throw new UnsupportedOperationException();
        }
    }

    private static class DataSource extends RichParallelSourceFunction<Tuple2<Long, Long>> implements CheckpointedFunction {

        private int taskid = 0;

        @Override
        public void open(Configuration parameters) throws Exception {
            taskid = getRuntimeContext().getIndexOfThisSubtask();

        }

        @Override
        public void run(SourceContext<Tuple2<Long, Long>> ctx) throws Exception {


            final long startTime = System.currentTimeMillis();

            final long numElements = 10000;
            final long numKeys = 1000;
            long val = 1L;
            long count = 0L;

            for (long i = 0; i < numElements; i++) {
                for (long j = 0; j < numKeys; j++) {
                    ctx.collect(new Tuple2(numKeys * taskid + j, 1L));
                }
            }

            final long endTime = System.currentTimeMillis();
            System.out.println("Took " + (endTime - startTime) + " msecs for " + numElements + " values");
        }

        @Override
        public void cancel() {

        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
        }

        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            OperatorStateStore stateStore = context.getOperatorStateStore();
            ListState<Tuple2<Long, Long>> stamp =
                    stateStore.getListState(new ListStateDescriptor<>("_VAN_", TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {
                    })));

        }
    }

}
