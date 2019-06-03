package io.github.spafka.stream;

import java.util.List;

public interface IService<I,O> {
     List<I> poll(List<Pipe<I>> inputQueues) throws Exception;
     boolean offer(List<Pipe<O>> outputQueues, List<O> outputs)  throws Exception ;
     void start();
}
