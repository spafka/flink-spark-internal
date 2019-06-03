package io.github.spafka.stream;

import java.util.concurrent.ArrayBlockingQueue;

public class ArrayBlockingQueuePipe<E> extends ArrayBlockingQueue<E> implements Pipe<E>{
    public ArrayBlockingQueuePipe(int capacity) {
        super(capacity);
    }
}