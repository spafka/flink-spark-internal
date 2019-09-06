package test;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.apache.flink.configuration.ConfigConstants.LOCAL_NUMBER_TASK_MANAGER;

public class StreamSuite {

    StreamExecutionEnvironment env;

    @Before
    public void init() {

        Configuration configuration = new Configuration();
        configuration.setInteger(RestOptions.PORT, 8080);
        configuration.setInteger(TaskManagerOptions.NUM_TASK_SLOTS, 10);
        configuration.setInteger(LOCAL_NUMBER_TASK_MANAGER, 2);


        configuration.setString(NettyShuffleEnvironmentOptions.NETWORK_BUFFERS_MEMORY_MIN, "1m");
        configuration.setInteger(NettyShuffleEnvironmentOptions.NETWORK_NUM_BUFFERS, 4096);
        configuration.setFloat(NettyShuffleEnvironmentOptions.NETWORK_BUFFERS_MEMORY_FRACTION, 0.5f);
        configuration.setString(CoreOptions.TMP_DIRS, "e://flink-tmp");
        configuration.setBoolean(NettyShuffleEnvironmentOptions.NETWORK_CREDIT_MODEL,false);

        env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);

        env.setBufferTimeout(0);

    }


    @Test
    public void userdefineSource() throws Exception {


        env.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceContext<String> ctx) throws Exception {

              while (true){
                  String ddf = "deep dark fantasy";

                  for (String s : ddf.split(" ")) {
                      ctx.collect(s);
                  }

                  TimeUnit.SECONDS.sleep(1);
              }
            }

            @Override
            public void cancel() {

            }

        }).slotSharingGroup("deep")
                .addSink(new RichSinkFunction<String>() {
                    @Override
                    public void setRuntimeContext(RuntimeContext t) {
                        super.setRuntimeContext(t);
                    }


                    public void invoke(String value) throws Exception {
                        System.out.println(value);
                    }

                }).slotSharingGroup("dark");


        env.execute("fantasy");
    }


}
