package com.github.spafka.concurrent.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.github.spafka.util.Utils;
import scala.Tuple2;
import scala.runtime.AbstractFunction0;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class quickstart {

    static final class ValueEvent {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public final static EventFactory<ValueEvent> EVENT_FACTORY = new EventFactory<ValueEvent>() {

            public ValueEvent newInstance() {
                return new ValueEvent();
            }
        };
    }

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        // Preallocate RingBuffer with 1024 ValueEvents
        Disruptor<ValueEvent> disruptor = new Disruptor<>(ValueEvent.EVENT_FACTORY, 1024, exec);
        // event will eventually be recycled by the Disruptor after it wraps
        final EventHandler<ValueEvent> handler = (event, sequence, endOfBatch) -> {
            System.out.println("Sequence: " + sequence);
            System.out.println("ValueEvent: " + event.getValue());
        };
        // Build dependency graph
        disruptor.handleEventsWith(handler);
        RingBuffer<ValueEvent> ringBuffer = disruptor.start();

        Object time = Utils.timeTakenMs(new AbstractFunction0<Object>() {
            @Override
            public Object apply() {
                for (long i = 0; i < 20000; i++) {
                    String uuid = UUID.randomUUID().toString();
                    // Two phase commit. Grab one of the 1024 slots
                    long seq = ringBuffer.next();
                    ValueEvent valueEvent = ringBuffer.get(seq);
                    valueEvent.setValue(uuid);
                    ringBuffer.publish(seq);
                }
                return null;
            }
        })._2;

        System.out.println(time);


        disruptor.shutdown();
        exec.shutdown();
    }
}
