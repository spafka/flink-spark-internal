package com.carmanconsulting.akka;

import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class HelloAkka extends LifecycleLogger {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final String DEFAULT_FORMAT = "Hello, %s!";

    private final String format;

//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    public static Props props() {
        return Props.create(HelloAkka.class);
    }

    public static Props props(String format) {
        return Props.create(HelloAkka.class, format);
    }

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public HelloAkka() {
        this(DEFAULT_FORMAT);
    }

    public HelloAkka(String format) {
        this.format = format;
    }

//----------------------------------------------------------------------------------------------------------------------
// Canonical Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.match(String.class, msg -> {
            sender().tell(String.format(format, msg), self());
        }).build();
    }
}
