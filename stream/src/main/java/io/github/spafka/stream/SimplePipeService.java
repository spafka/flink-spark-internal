package io.github.spafka.stream;


import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class SimplePipeService<I, O> extends AbstractPipeService<I, O>{

    @Override
    public   List<I> poll(List<Pipe<I>> inputQueues) throws Exception {
        inputsList.clear();
        Pipe<I> inputPipe = inputQueues.get(0);

        I event = inputPipe.poll(1, TimeUnit.MILLISECONDS);
        if (event != null) {
            inputsList.add(event);
            return inputsList;
        } else {
            return null;
        }
    }

    @Override
    public  boolean offer(List<Pipe<O>> outputQueues, List<O> outputs) throws Exception {
        Pipe<O> outputQueue = outputQueues.get(0);
        O event = outputs.get(0);
        return outputQueue.offer(event, 1, TimeUnit.MILLISECONDS);
    }


}