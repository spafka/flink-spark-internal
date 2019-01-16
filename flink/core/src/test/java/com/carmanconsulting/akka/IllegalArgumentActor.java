package com.carmanconsulting.akka;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public abstract class IllegalArgumentActor extends LifecycleLogger {
//----------------------------------------------------------------------------------------------------------------------
// Canonical Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.matchAny(x -> {
            throw new IllegalArgumentException(String.valueOf(x));
        }).build();
    }
}
