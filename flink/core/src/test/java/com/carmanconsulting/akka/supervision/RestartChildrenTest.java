package com.carmanconsulting.akka.supervision;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.carmanconsulting.akka.AkkaTestCase;
import com.carmanconsulting.akka.DevNull;
import com.carmanconsulting.akka.LifecycleLogger;
import org.junit.Test;
import scala.Option;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class RestartChildrenTest extends AkkaTestCase {

    @Test
    public void testRestartingChildren() {
        final ActorRef parent = system().actorOf(Props.create(Parent.class), "parent");
        parent.tell("Hello", testActor());
        expectNoMsg();
    }



//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    public static class Child extends DevNull {

    }

    public static class Parent extends LifecycleLogger {

        @Override
        public void preStart() throws Exception {
            super.preStart();
            context().actorOf(Props.create(Child.class), "child1");
            context().actorOf(Props.create(Child.class), "child2");
        }

        @Override
        public PartialFunction<Object, BoxedUnit> receive() {
            return ReceiveBuilder.matchAny(x -> {
                throw new IllegalArgumentException(String.valueOf(x));

            }).build();
        }

        @Override
        public void preRestart(Throwable reason, Option<Object> message) {
            log.info("{} preRestart({}, {})", instanceNumber, reason, message);
            postStop();
        }

        @Override
        public void postRestart(Throwable reason) throws Exception {
            log.info("{} postRestart({})", instanceNumber, reason);
        }
    }
}
