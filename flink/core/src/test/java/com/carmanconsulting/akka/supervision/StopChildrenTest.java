package com.carmanconsulting.akka.supervision;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.carmanconsulting.akka.AkkaTestCase;
import com.carmanconsulting.akka.DevNull;
import com.carmanconsulting.akka.IllegalArgumentActor;
import org.junit.Test;

public class StopChildrenTest extends AkkaTestCase {
//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void testStoppingChildren() {
        final ActorRef parent = system().actorOf(Props.create(Parent.class), "parent");
        parent.tell("Hello", testActor());
        expectNoMsg();
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    public static class Child extends DevNull {
    }

    public static class Parent extends IllegalArgumentActor {
        @Override
        public void preStart() throws Exception {
            super.preStart();
            context().actorOf(Props.create(Child.class), "child1");
            context().actorOf(Props.create(Child.class), "child2");
        }
    }
}
