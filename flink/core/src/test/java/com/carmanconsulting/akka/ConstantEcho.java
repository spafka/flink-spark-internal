package com.carmanconsulting.akka;

import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class ConstantEcho extends LifecycleLogger {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final Object reply;

//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    public static Props props(Object reply) {
        return Props.create(ConstantEcho.class, reply);
    }

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public ConstantEcho(Object reply) {
        this.reply = reply;
    }

//----------------------------------------------------------------------------------------------------------------------
// Canonical Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.matchAny(x -> sender().tell(reply, self())).build();
    }
}
