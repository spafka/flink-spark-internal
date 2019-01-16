package com.carmanconsulting.akka.voting.actor;

import akka.testkit.TestActorRef;
import com.carmanconsulting.akka.AkkaTestCase;
import com.carmanconsulting.akka.voting.msg.AddVote;
import com.carmanconsulting.akka.voting.msg.GetVoteCount;
import com.carmanconsulting.akka.voting.msg.VoteAdded;
import com.carmanconsulting.akka.voting.msg.VoteCount;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VoteCounterTest extends AkkaTestCase {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final String DITKA = "Mike Ditka";

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void testAddVote() {
        TestActorRef<VoteCounter> ref = new TestActorRef<>(system(), VoteCounter.props(DITKA), testActor(), DITKA);
        ref.tell(new AddVote(), testActor());
        VoteAdded voteAdded = expectMsgClass(VoteAdded.class);
        assertEquals(DITKA, voteAdded.getCandidate());

        VoteCounter voteCounter = ref.underlyingActor();
        assertEquals(1, voteCounter.getCount());
        assertEquals(DITKA, voteCounter.getCandidate());
    }

    @Test
    public void testGetVoteCount() {
        TestActorRef<VoteCounter> ref = new TestActorRef<>(system(), VoteCounter.props(DITKA), testActor(), DITKA);
        ref.tell(new GetVoteCount(), testActor());
        VoteCount voteCount = expectMsgClass(VoteCount.class);
        assertEquals(DITKA, voteCount.getCandidate());
        assertEquals(0, voteCount.getCount());

        ref.tell(new AddVote(), testActor());
        ref.tell(new AddVote(), testActor());
        ref.tell(new AddVote(), testActor());
        receiveN(3);

        ref.tell(new GetVoteCount(), testActor());
        voteCount = expectMsgClass(VoteCount.class);
        assertEquals(DITKA, voteCount.getCandidate());
        assertEquals(3, voteCount.getCount());
    }
}