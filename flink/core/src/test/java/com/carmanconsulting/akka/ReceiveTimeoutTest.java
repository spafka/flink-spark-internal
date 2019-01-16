package com.carmanconsulting.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.japi.pf.ReceiveBuilder;
import org.junit.Test;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

public class ReceiveTimeoutTest extends AkkaTestCase {
//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void testReceiveTimeout() throws Exception {
        system().actorOf(Props.create(ReceiveTimeoutActor.class, testActor()), "receiveTimeout");
        expectMsgEquals("ReceiveTimeout!");
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    public static class ReceiveTimeoutActor extends AbstractActor {
        private final ActorRef listener;

        public ReceiveTimeoutActor(ActorRef listener) {
            this.listener = listener;
        }

        @Override
        public void preStart() throws Exception {
            context().setReceiveTimeout(Duration.create("500 milliseconds"));
        }

        @Override
        public PartialFunction<Object, BoxedUnit> receive() {
            return ReceiveBuilder
                    .match(ReceiveTimeout.class, timeout -> listener.tell("ReceiveTimeout!", ReceiveTimeoutActor.this.self()))
                    .matchAny(this::unhandled).build();
        }
    }
}
