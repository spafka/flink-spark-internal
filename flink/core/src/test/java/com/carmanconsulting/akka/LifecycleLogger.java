package com.carmanconsulting.akka;

import java.util.concurrent.atomic.AtomicInteger;

import akka.actor.AbstractActorWithStash;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.Option;

public abstract class LifecycleLogger extends AbstractActorWithStash {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private static final AtomicInteger instanceNumberSequence = new AtomicInteger();
    protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    protected final int instanceNumber = instanceNumberSequence.incrementAndGet();

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    protected LifecycleLogger() {
        log.info("{} new {}()", instanceNumber, getClass().getSimpleName());
    }

//----------------------------------------------------------------------------------------------------------------------
// Canonical Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void postRestart(Throwable reason) throws Exception {
        log.info("{} postRestart({})", instanceNumber, reason);
        super.postRestart(reason);
    }

    @Override
    public void postStop() {
        log.info("{} postStop()", instanceNumber);
        super.postStop();
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) {
        log.info("{} preRestart({}, {})", instanceNumber, reason, message);
        super.preRestart(reason, message);
    }

    @Override
    public void preStart() throws Exception {
        log.info("{} preStart()", instanceNumber);
        super.preStart();
    }
}
