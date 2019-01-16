package com.carmanconsulting.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import org.junit.Test;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import static org.junit.Assert.assertEquals;

public class ForwardTest extends AkkaTestCase {
//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void testForwarding() {
        ActorRef forwarder = system().actorOf(Props.create(Forwarder.class, testActor()));
        forwarder.tell("Hello", testActor());
        expectMsgEquals("Hello");
        assertEquals(testActor(), getLastSender());
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    public static class Forwarder extends AbstractActor {
        private final ActorRef target;

        public Forwarder(ActorRef target) {
            this.target = target;
        }

        @Override
        public PartialFunction<Object, BoxedUnit> receive() {
            return ReceiveBuilder.matchAny(x -> target.forward(x, context())).build();
        }
    }
}
