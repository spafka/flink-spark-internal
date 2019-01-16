package com.carmanconsulting.akka;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import org.junit.Test;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class StoppingTest extends AkkaTestCase {
//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void testKill() {
        ActorRef echo = system().actorOf(Props.create(KillOnHalt.class), "kill");
        watch(echo);
        echo.tell("Hello", testActor());
        echo.tell("Halt", testActor());
        echo.tell("Hello", testActor());

        expectMsgEquals("Hello");
        expectMsgEquals("Halt");
        expectMsgEquals("Hello");
        expectMsgClass(Terminated.class);
        expectNoMsg();
    }

    @Test
    public void testPoisonPill() {
        ActorRef echo = system().actorOf(Props.create(PoisonPillOnHalt.class), "poisonPill");
        watch(echo);
        echo.tell("Hello", testActor());
        echo.tell("Halt", testActor());
        echo.tell("Hello", testActor());

        expectMsgEquals("Hello");
        expectMsgEquals("Halt");
        expectMsgEquals("Hello");
        expectMsgClass(Terminated.class);
        expectNoMsg();
    }

    @Test
    public void testStopMethod() {
        ActorRef echo = system().actorOf(Props.create(StopOnHalt.class), "stopMethod");
        watch(echo);
        echo.tell("Hello", testActor());
        echo.tell("Halt", testActor());
        echo.tell("Hello", testActor());


        expectMsgEquals("Hello");
        expectMsgEquals("Halt");
        expectMsgClass(Terminated.class);
        expectNoMsg();
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    public static class KillOnHalt extends LifecycleLogger {
        @Override
        public PartialFunction<Object, BoxedUnit> receive() {
            return ReceiveBuilder
                    .matchEquals("Halt", msg -> {
                        sender().tell(msg, self());
                        self().tell(Kill.getInstance(), self());
                    })
                    .match(String.class, msg -> sender().tell(msg, self()))
                    .build();
        }
    }

    public static class PoisonPillOnHalt extends LifecycleLogger {
        @Override
        public PartialFunction<Object, BoxedUnit> receive() {
            return ReceiveBuilder
                    .matchEquals("Halt", msg -> {
                        sender().tell(msg, self());
                        self().tell(PoisonPill.getInstance(), self());
                    })
                    .match(String.class, msg -> sender().tell(msg, self()))
                    .build();
        }
    }

    public static class StopOnHalt extends LifecycleLogger {
        @Override
        public PartialFunction<Object, BoxedUnit> receive() {
            return ReceiveBuilder
                    .matchEquals("Halt", msg -> {
                        sender().tell(msg, self());
                        context().stop(self());
                    })
                    .match(String.class, msg -> sender().tell(msg, self()))
                    .build();
        }
    }
}
