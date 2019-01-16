package com.carmanconsulting.akka;

import java.util.Arrays;
import java.util.List;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Identify;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ActorSelectionTest extends AkkaTestCase {
//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void testIdentifying() {
        ActorRef actorRef = getSystem().actorOf(ConstantEcho.props("foo"), "foo");

        final ActorSelection selection = getSystem().actorSelection("/user/foo");
        selection.tell(new Identify("identifyFoo"), testActor());

        final Object[] seq = receiveN(1);

        ActorIdentity identity = (ActorIdentity) seq[0];
        assertEquals("identifyFoo", identity.correlationId());

        identity.getRef().tell("baz", testActor());
        expectMsgEquals("foo");
    }

    @Test
    public void testSelectingMultipleActors() {
        system().actorOf(ConstantEcho.props("foo"), "foo");
        system().actorOf(ConstantEcho.props("bar"), "bar");

        final ActorSelection selection = system().actorSelection("/user/*");

        selection.tell("baz", testActor());
        final List<Object> messages = Arrays.asList(receiveN(2));
        assertTrue(messages.contains("foo"));
        assertTrue(messages.contains("bar"));
    }

    @Test
    public void testSelectingOneActor() {
        system().actorOf(ConstantEcho.props("foo"), "foo");
        system().actorOf(ConstantEcho.props("bar"), "bar");
        ActorSelection selection = system().actorSelection("/user/foo");
        selection.tell("baz", testActor());
        expectMsgEquals("foo");
    }
}
