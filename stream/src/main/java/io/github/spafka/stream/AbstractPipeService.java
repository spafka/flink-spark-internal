package io.github.spafka.stream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class AbstractPipeService<I, O> implements IService<I,O> {

    volatile boolean stopped;
    Thread thread;

    List<Pipe<I>> inputPipes;
    List<Pipe<O>> outputPipes;
    List inputsList;

    private boolean pipe(boolean checkStop, boolean exitWhenNoInput) throws Exception {
        // ......
        List<I> inputs = poll(inputPipes);
        List<O> outputs = process(inputs);
        return offer(outputPipes, outputs);
        // ......
    }

    abstract List<O> process(List<I> inputs);

    @Override
    public void start() {
        thread = new Thread(() -> {
            try {
                beforeStart();

                while (!stopped) {
                    try {
                        if (pipe(true, false)) {
                            break;
                        }
                    } catch (Exception e) {
                        log.error("unexpected exception", e);
                    }
                }

                beforeShutdown();

            } catch (Exception e) {
                log.error("unexpected exception", e);
            }
        });
        thread.start();
    }

    abstract void beforeShutdown();

    abstract void beforeStart();
}
