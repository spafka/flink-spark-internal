package com.carmanconsulting.akka;

import akka.actor.ActorRef;
import org.junit.Test;

public class HelloAkkaTest extends AkkaTestCase {

    @Test
    public void testHelloAkka() {
        ActorRef hello = system().actorOf(HelloAkka.props(), "hello");
        hello.tell("Akka", testActor());
        expectMsgEquals("Hello, Akka!");
    }
}
