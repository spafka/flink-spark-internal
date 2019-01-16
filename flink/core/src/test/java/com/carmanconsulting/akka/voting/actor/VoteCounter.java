package com.carmanconsulting.akka.voting.actor;

import akka.actor.Props;
import com.carmanconsulting.akka.LifecycleLogger;
import com.carmanconsulting.akka.voting.msg.AddVote;
import com.carmanconsulting.akka.voting.msg.GetVoteCount;
import com.carmanconsulting.akka.voting.msg.VoteAdded;
import com.carmanconsulting.akka.voting.msg.VoteCount;

import static akka.japi.pf.ReceiveBuilder.match;

public class VoteCounter extends LifecycleLogger {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final String candidate;
    private int count;

//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    public static Props props(String candidate) {
        return Props.create(VoteCounter.class, candidate);
    }

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public VoteCounter(String candidate) {
        this.candidate = candidate;
        receive(match(AddVote.class, this::addVote)
                .match(GetVoteCount.class, this::getVoteCount)
                .build());
    }

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public String getCandidate() {
        return candidate;
    }

    public int getCount() {
        return count;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    private void addVote(AddVote addVote) {
        count++;
        sender().tell(new VoteAdded(candidate), self());
    }

    private void getVoteCount(GetVoteCount getVoteCount) {
        sender().tell(new VoteCount(candidate, count), self());
    }
}
