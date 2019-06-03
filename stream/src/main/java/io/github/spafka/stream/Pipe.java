package io.github.spafka.stream;

import java.util.concurrent.TimeUnit;

public interface Pipe<E> {
    E poll(long timeout, TimeUnit unit) throws InterruptedException;
    boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException;
}
