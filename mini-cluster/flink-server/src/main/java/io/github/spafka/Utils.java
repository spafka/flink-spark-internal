package io.github.spafka;


import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class Utils {

	public static StreamExecutionEnvironment getStreamEnv(){

		String CHECKPOINTS_DIRECTORY = "file:///tmp/state/checkpoint";
		String SAVEPOINT_DIRECTORY = "file:///tmp/state/savepoint";


		try {
			FileUtils.deleteFileOrDirectory(new File("/tmp/state"));
		} catch (IOException e) {

		}


		Configuration configuration = new Configuration();

		// flink >1.9 CheckpointingOptions
		configuration.setString("state.checkpoints.dir", CHECKPOINTS_DIRECTORY);

		configuration.setString("state.savepoints.dir", SAVEPOINT_DIRECTORY);
		configuration.setBoolean("state.backend.async", true);
		configuration.setInteger("state.checkpoints.num-retained", 10);
		configuration.setString("state.backend", "rocksdb");
		configuration.setInteger("web.port", 8081);
		configuration.setBoolean("state.backend.incremental",true);
		configuration.setInteger(ConfigConstants.LOCAL_NUMBER_TASK_MANAGER,4); // 4 tm
		configuration.setLong("metrics.latency.interval",-1L);


		StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);

		env.getCheckpointConfig().setCheckpointInterval(Long.MAX_VALUE);
		//env.getCheckpointConfig().setCheckpointInterval(10000L);
		env.setBufferTimeout(0L);

		try {
			env.setStateBackend(new RocksDBStateBackend(CHECKPOINTS_DIRECTORY));
		} catch (IOException e) {

		}

		return env;
	}
}
