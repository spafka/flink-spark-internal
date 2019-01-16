package com.carmanconsulting.akka;

import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class DevNull extends LifecycleLogger {
//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    public static Props props() {
        return Props.create(DevNull.class);
    }

//----------------------------------------------------------------------------------------------------------------------
// Canonical Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.matchAny(this::unhandled).build();
    }
}
