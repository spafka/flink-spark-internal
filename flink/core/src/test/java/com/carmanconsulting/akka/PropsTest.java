package com.carmanconsulting.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Creator;
import org.junit.Test;

public class PropsTest extends AkkaTestCase {

    @Test
    public void testWithNoArguments() {
        ActorRef hello = system().actorOf(Props.create(HelloAkka.class), "hello");
        hello.tell("Akka", testActor());
        expectMsgEquals("Hello, Akka!");
    }

    @Test
    public void testWithArguments() {
        ActorRef hello = system().actorOf(Props.create(HelloAkka.class, "Hola, %s!"), "hello");
        hello.tell("Akka", testActor());
        expectMsgEquals("Hola, Akka!");
    }

    @Test
    public void testWithCreator() {
        ActorRef hello = system().actorOf(Props.create(new HelloAkkaActorCreator()), "hello");
        hello.tell("Akka", testActor());
        expectMsgEquals("Bonjour, Akka!");
    }

    public static class HelloAkkaActorCreator implements Creator<HelloAkka> {
        @Override
        public HelloAkka create() throws Exception {
            return new HelloAkka("Bonjour, %s!");
        }
    }
}
