package com.carmanconsulting.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import akka.testkit.TestKit;
import org.junit.After;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

public abstract class AkkaTestCase extends JavaTestKit {
//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public AkkaTestCase() {
        super(ActorSystem.apply("AkkaTesting"));
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @After
    public void shutdownActorSystem() {
        LoggerFactory.getLogger(getClass()).info("Shutting down ActorSystem...");
        TestKit.shutdownActorSystem(getSystem(), Duration.create("3 seconds"), true);
    }

    public ActorSystem system() {
        return getSystem();
    }

    public ActorRef testActor() {
        return getTestActor();
    }
}
