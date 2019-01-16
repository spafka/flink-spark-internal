package com.carmanconsulting.akka;

import java.util.concurrent.TimeUnit;

import akka.actor.*;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.AskTimeoutException;
import akka.util.Timeout;
import org.junit.Test;
import scala.PartialFunction;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AskTest extends AkkaTestCase {
//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void testAskWhenActorThrowsException() {
        ActorRef failure = system().actorOf(Props.create(ExceptionActor.class));
        final Timeout timeout = new Timeout(Duration.create(200, TimeUnit.MILLISECONDS));
        final Future<Object> future = ask(failure, "Akka", timeout);
        future.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable failure) throws Throwable {
                assertTrue(failure instanceof AskTimeoutException);
                testActor().tell("Failure", testActor());
            }
        }, system().dispatcher());
        expectMsgEquals("Failure");
    }

    @Test
    public void testAskWithFailure() {
        ActorRef failure = system().actorOf(Props.create(FailureActor.class));
        final Timeout timeout = new Timeout(Duration.create(200, TimeUnit.MILLISECONDS));
        final Future<Object> future = ask(failure, "Akka", timeout);
        future.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable failure) throws Throwable {
                assertTrue(failure instanceof IllegalArgumentException);
                testActor().tell("Failure", testActor());
            }
        }, system().dispatcher());
        expectMsgEquals("Failure");
    }

    @Test
    public void testAskWithTimeout() {
        ActorRef devNull = system().actorOf(DevNull.props());
        final Timeout timeout = new Timeout(Duration.create(200, TimeUnit.MILLISECONDS));
        final Future<Object> future = ask(devNull, "Akka", timeout);
        future.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable failure) throws Throwable {
                assertTrue(failure instanceof AskTimeoutException);
                testActor().tell("Failure", testActor());
            }
        }, system().dispatcher());
        expectMsgEquals("Failure");
    }

    @Test
    public void testSuccessfulAsk() {
        ActorRef hello = system().actorOf(HelloAkka.props());
        final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
        final Future<Object> future = ask(hello, "Akka", timeout);
        future.onSuccess(new OnSuccess<Object>() {
            public void onSuccess(Object result) {
                // Do something!
                testActor().tell("Success", testActor());
            }
        }, system().dispatcher());
        expectMsgEquals("Success");
    }

    @Test
    public void testWithAwait() throws Exception {
        ActorRef hello = system().actorOf(HelloAkka.props());
        final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
        final Future<Object> future = ask(hello, "Akka", timeout);
        final Object result = Await.result(future, timeout.duration());
        assertEquals("Hello, Akka!", result);
    }

    @Test
    public void testWithPipe() {
        ActorRef hello = system().actorOf(HelloAkka.props());
        final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
        final Future<Object> future = ask(hello, "Akka", timeout);
        final ActorRef recipient = testActor();
        pipe(future, system().dispatcher()).to(recipient);
        expectMsgEquals("Hello, Akka!");
    }

    @Test
    public void testWithPipeOnFailure() {
        ActorRef failure = system().actorOf(Props.create(FailureActor.class));
        final Timeout timeout = new Timeout(Duration.create(200, TimeUnit.MILLISECONDS));
        final Future<Object> future = ask(failure, "Akka", timeout);
        pipe(future, system().dispatcher()).to(testActor());
        Status.Failure actual = expectMsgClass(Status.Failure.class);
        assertTrue(actual.cause() instanceof IllegalArgumentException);
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    public static class ExceptionActor extends IllegalArgumentActor {
    }

    public static class FailureActor extends AbstractActor {
        @Override
        public PartialFunction<Object, BoxedUnit> receive() {
            return ReceiveBuilder.matchAny(x -> sender().tell(new Status.Failure(new IllegalArgumentException()), self())).build();
        }
    }
}
